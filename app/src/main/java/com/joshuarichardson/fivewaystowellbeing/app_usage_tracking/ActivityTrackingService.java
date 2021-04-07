package com.joshuarichardson.fivewaystowellbeing.app_usage_tracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.ActivityTracking;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.AppActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AppActivity;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.PhysicalActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.core.app.NotificationCompat;
import dagger.hilt.android.AndroidEntryPoint;

import static android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED;
import static android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED;

@AndroidEntryPoint
public class ActivityTrackingService extends Service {

    @Inject
    WellbeingDatabase db;

    private final int DURATION_THRESHOLD = 10000;
    public static final String APP_TRACKING_CHANNEL_ID = "app_usage_channel";
    private static final int APP_USAGE_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManager notification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel(APP_TRACKING_CHANNEL_ID, getString(R.string.app_usage_channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, APP_TRACKING_CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_star_outline_logo);

        UsageStatsManager usage = (UsageStatsManager) getSystemService(Service.USAGE_STATS_SERVICE);
        PackageManager packageManager = getPackageManager();
        startForeground(APP_USAGE_ID, builder.build());

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            db.appActivityDao().deleteAll();
        });

        Thread thread = new Thread(() -> {
            while (true) {
                Calendar cal = Calendar.getInstance();
                long currentTime = cal.getTimeInMillis();
                cal.add(Calendar.MILLISECOND, -DURATION_THRESHOLD);
                long startTime = cal.getTimeInMillis();

                UsageEvents stats = usage.queryEvents(startTime, currentTime);

                while(stats.hasNextEvent()) {
                    UsageEvents.Event event = new UsageEvents.Event();

                    stats.getNextEvent(event);

                    WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                        AppActivityDao appUsageDao = db.appActivityDao();
                        AppActivity appActivity = appUsageDao.getPhysicalActivityByType(event.getPackageName());

                        if (appActivity == null) {
                            appActivity = new AppActivity(event.getPackageName(), 0, 0, 0, 0, false);
                            appUsageDao.insert(appActivity);
                        }

                        String packageId = event.getPackageName();

                        // Get the timestamp when the activity started
                        if (event.getEventType() == ACTIVITY_RESUMED) {
                            if (appActivity.getStartTime() == 0) {
                                // The first thing that happens in an app is it resumes so ideal to update the start time and resumed time
                                appUsageDao.updateStartAndLastResumedTime(packageId, event.getTimeStamp(), event.getTimeStamp());
                            } else {
                                appUsageDao.updateUsageTime(packageId, event.getTimeStamp());
                            }
                        }

                        // Get the timestamp when the activity paused
                        if (event.getEventType() == ACTIVITY_PAUSED) {
                            // The last thing that happens in an app is that it pauses
                            appUsageDao.updateEndTime(packageId, event.getTimeStamp());
                        }
                    });
                }

                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {

                    long durationDifference = currentTime - (DURATION_THRESHOLD/2);
                    // Get a list of all the activities that are finished since last time
                    List<AppActivity> finishedApps = db.appActivityDao().getFinishedActivities(durationDifference);

                    for(AppActivity app : finishedApps) {
                        String packageId = app.getPackageName();

                        ApplicationInfo info;
                        try {
                            info = packageManager.getApplicationInfo(packageId, 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            continue;
                        }
                        String name = (String) packageManager.getApplicationLabel(info);

                        // If longer than duration then need to update the physical activity details
                        if ((app.getEndTime() - app.getStartTime()) > DURATION_THRESHOLD) {
                            PhysicalActivity activity = db.physicalActivityDao().getPhysicalActivityByType(app.getPackageName());
                            if(activity == null) {
                                return;
                            }

                            // Update the times and pending status of the activity that has just been added
                            db.physicalActivityDao().updateStartTime(packageId, app.getStartTime());
                            db.physicalActivityDao().updateEndTime(packageId, app.getEndTime());
                            db.physicalActivityDao().updateIsPendingStatus(packageId, true);

                            ActivityTracking tracking = new ActivityTracking();
                            tracking.sendActivityNotification(this, activity.getActivityId(), activity.getStartTime(), activity.getEndTime(), packageId, name);


                            db.appActivityDao().setPending(packageId, true);
                        }

                        // Reset the record
                        db.appActivityDao().deleteWithPackageId(packageId);
                    }
                });
                try {
                    Thread.sleep(DURATION_THRESHOLD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
//        stopForeground(true);
            return super.onStartCommand(intent, flags, startId);
        };

    private String convertToHoursMinutesSeconds(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds -= minutes * 60;
        long hours = minutes / 60;
        minutes -= hours * 60;

        return String.format(Locale.getDefault(), "%dh %dm %ds", hours, minutes, seconds);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}