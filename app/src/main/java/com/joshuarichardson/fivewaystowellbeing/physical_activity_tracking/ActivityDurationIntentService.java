package com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AutomaticActivity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.preference.PreferenceManager;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActivityDurationIntentService extends IntentService {

    public static final String START_ACTIVITY = "com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking.action.start";
    public static final String END_ACTIVITY = "com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking.action.end";

    @Inject
    WellbeingDatabase db;

    ActivityTracking tracking = new ActivityTracking();

    public ActivityDurationIntentService() {
        super("ActivityDurationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String eventType = intent.getExtras().getString("event_type", null);
        long nanoSeconds = intent.getExtras().getLong("elapsed_time", 0);

        if(eventType == null) {
            return;
        }

        long activityTypeThreshold;
        boolean activityTypeEnabled;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        switch (eventType) {
            case AutomaticActivityTypes.WALK:
                activityTypeThreshold = preferences.getInt("notification_walk_duration", 10);
                activityTypeEnabled = preferences.getBoolean("notification_walk_enabled", false);
                break;
            case AutomaticActivityTypes.RUN:
                activityTypeThreshold = preferences.getInt("notification_run_duration", 10);
                activityTypeEnabled = preferences.getBoolean("notification_run_enabled", false);
                break;
            case AutomaticActivityTypes.CYCLE:
                activityTypeThreshold = preferences.getInt("notification_cycle_duration", 10);
                activityTypeEnabled = preferences.getBoolean("notification_cycle_enabled", false);
                break;
            case AutomaticActivityTypes.VEHICLE:
                activityTypeThreshold = preferences.getInt("notification_drive_duration", 10);
                activityTypeEnabled = preferences.getBoolean("notification_drive_enabled", false);
                break;
            default:
                activityTypeThreshold = preferences.getInt("notification_app_duration", 10);
                activityTypeEnabled = preferences.getBoolean("notification_app_enabled", false);
        }

        if(!activityTypeEnabled) {
            return;
        }

        // Convert minutes to ms
        activityTypeThreshold *= 60000;
        long finalActivityTypeThreshold = activityTypeThreshold;

        switch(intent.getAction()) {
            case START_ACTIVITY:
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    AutomaticActivity automaticActivity = this.db.physicalActivityDao().getPhysicalActivityByTypeWithAssociatedActivity(eventType);

                    if (automaticActivity == null) {
                        return;
                    }

                    long eventTimeMillis = calculateEventTimeMillis(nanoSeconds);

                    // Only update the start time if the time of the event starting is half the threshold to log the activity
                    // This means that you can have a small break and it still be logged as the same activity
                    if (automaticActivity.getStartTime() <= 0 || (eventTimeMillis - automaticActivity.getEndTime()) > (finalActivityTypeThreshold /2)) {
                        this.db.physicalActivityDao().updateStartTime(eventType, eventTimeMillis);
                        this.db.physicalActivityDao().updateIsNotificationConfirmedStatus(eventType, false);
                    }
                });
                break;
            case END_ACTIVITY:
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    // Still update the end time - even if it wasn't long enough
                    this.db.physicalActivityDao().updateEndTime(eventType, calculateEventTimeMillis(nanoSeconds));
                    AutomaticActivity activity = this.db.physicalActivityDao().getPhysicalActivityByTypeWithAssociatedActivity(eventType);

                    if (activity == null) {
                        return;
                    }

                    long activityDuration = activity.getEndTime() - activity.getStartTime();
                    if(activityDuration > finalActivityTypeThreshold && !activity.isNotificationConfirmed()) {
                        this.db.physicalActivityDao().updateIsPendingStatus(eventType, true);
                        this.tracking.sendActivityNotification(this, activity.getActivityId(), activity.getStartTime(), activity.getEndTime(), eventType, null, null);
                    }
                });

                break;
            default:
                break;
        }
    }

    private long calculateEventTimeMillis(long eventNanos) {
        long actualNanos = SystemClock.elapsedRealtimeNanos();
        long difference = actualNanos - eventNanos;
        // Reference https://stackoverflow.com/a/21600253/13496270
        long differenceMillis = TimeUnit.MILLISECONDS.convert(difference, TimeUnit.NANOSECONDS);
        return new Date().getTime() - differenceMillis;
    }
}