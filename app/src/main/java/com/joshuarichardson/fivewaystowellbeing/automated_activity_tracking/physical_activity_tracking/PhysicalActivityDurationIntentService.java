package com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.AutomaticActivityTypes;
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.AutomaticNotificationHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AutomaticActivity;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.preference.PreferenceManager;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * An intent service for calculating activity duration and taking appropriate actions.
 * Tables will be updated accordingly and notifications will be sent if necessary.
 */
@AndroidEntryPoint
public class PhysicalActivityDurationIntentService extends IntentService {

    public static final String START_ACTIVITY = "com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking.action.start";
    public static final String END_ACTIVITY = "com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking.action.end";

    @Inject
    WellbeingDatabase db;

    @Inject
    AutomaticNotificationHelper automaticNotificationHelper;

    public PhysicalActivityDurationIntentService() {
        super("ActivityDurationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String eventType = intent.getExtras().getString("event_type", null);
        long nanoSeconds = intent.getExtras().getLong("elapsed_time", 0);

        if(eventType == null) {
            return;
        }

        if(!isActivityTypeEnabled(eventType)) {
            return;
        }

        // Convert minutes to ms
        long finalActivityTypeThreshold = getMinimumActivityDuration(eventType) * 60000;

        switch(intent.getAction()) {
            case START_ACTIVITY:
                WellbeingDatabaseModule.databaseExecutor.execute(() -> {
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
                WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                    // Still update the end time - even if it wasn't long enough
                    this.db.physicalActivityDao().updateEndTime(eventType, calculateEventTimeMillis(nanoSeconds));
                    AutomaticActivity activity = this.db.physicalActivityDao().getPhysicalActivityByTypeWithAssociatedActivity(eventType);

                    if (activity == null) {
                        return;
                    }

                    long activityDuration = activity.getEndTime() - activity.getStartTime();
                    if(activityDuration > finalActivityTypeThreshold && !activity.isNotificationConfirmed()) {
                        this.db.physicalActivityDao().updateIsPendingStatus(eventType, true);
                        this.automaticNotificationHelper.sendSuggestedActivityNotification(activity.getActivityId(), activity.getStartTime(), activity.getEndTime(), eventType, null, null);
                    }
                });

                break;
            default:
                break;
        }
    }

    /**
     * Get the minimum activity duration in minutes
     *
     * @param eventType The name of the event
     * @return The minimum duration in minutes
     */
    private long getMinimumActivityDuration(String eventType) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        switch (eventType) {
            case AutomaticActivityTypes.WALK:
                return preferences.getInt("notification_walk_duration", 10);
            case AutomaticActivityTypes.RUN:
                return preferences.getInt("notification_run_duration", 10);
            case AutomaticActivityTypes.CYCLE:
                return preferences.getInt("notification_cycle_duration", 10);
            case AutomaticActivityTypes.VEHICLE:
                return preferences.getInt("notification_drive_duration", 10);
            default:
                return preferences.getInt("notification_app_duration", 10);
        }
    }

    /**
     * See if activity type is enabled for automatic notifications
     * @param eventType The name of the event
     * @return The enabled status
     */
    private boolean isActivityTypeEnabled(String eventType) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        switch (eventType) {
            case AutomaticActivityTypes.WALK:
                return preferences.getBoolean("notification_walk_enabled", false);
            case AutomaticActivityTypes.RUN:
                return preferences.getBoolean("notification_run_enabled", false);
            case AutomaticActivityTypes.CYCLE:
                return preferences.getBoolean("notification_cycle_enabled", false);
            case AutomaticActivityTypes.VEHICLE:
                return preferences.getBoolean("notification_drive_enabled", false);
            default:
                return preferences.getBoolean("notification_app_enabled", false);
        }
    }

    /**
     * Calculate the event timestamp in milliseconds
     *
     * @param eventNanos The nano-seconds for the event
     * @return The time in milliseconds
     */
    private long calculateEventTimeMillis(long eventNanos) {
        long actualNanos = SystemClock.elapsedRealtimeNanos();
        long difference = actualNanos - eventNanos;
        // Reference https://stackoverflow.com/a/21600253/13496270
        long differenceMillis = TimeUnit.MILLISECONDS.convert(difference, TimeUnit.NANOSECONDS);
        return Calendar.getInstance().getTimeInMillis() - differenceMillis;
    }
}