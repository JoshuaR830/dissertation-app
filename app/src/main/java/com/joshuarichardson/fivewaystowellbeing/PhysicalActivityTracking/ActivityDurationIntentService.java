package com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;

import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.PhysicalActivity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking.ActivityReceiver.DURATION_THRESHOLD;

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

        switch(intent.getAction()) {
            case START_ACTIVITY:
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    PhysicalActivity physicalActivity = this.db.physicalActivityDao().getPhysicalActivityByType(eventType);
                    long eventTimeMillis = calculateEventTimeMillis(nanoSeconds);

                    // Only update the start time if the time of the event starting is half the threshold to log the activity
                    // This means that you can have a small break and it still be logged as the same activity
                    if (eventTimeMillis - physicalActivity.getEndTime() > (DURATION_THRESHOLD/2)) {
                        this.db.physicalActivityDao().updateStartTime(eventType, eventTimeMillis);
                    }
                });
                break;
            case END_ACTIVITY:
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    // Still update the end time - even if it wasn't long enough
                    this.db.physicalActivityDao().updateEndTime(eventType, calculateEventTimeMillis(nanoSeconds));
                    PhysicalActivity activity = this.db.physicalActivityDao().getPhysicalActivityByType(eventType);
                    long activityDuration = activity.getEndTime() - activity.getStartTime();

                    if(activityDuration > DURATION_THRESHOLD) {
                        this.db.physicalActivityDao().updateIsPendingStatus(eventType, true);
                        this.tracking.sendActivityNotification(this, activity.getActivityId(), activity.getStartTime(), activity.getEndTime(), eventType);
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