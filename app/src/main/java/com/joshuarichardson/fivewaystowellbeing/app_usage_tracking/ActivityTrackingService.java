package com.joshuarichardson.fivewaystowellbeing.app_usage_tracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import com.joshuarichardson.fivewaystowellbeing.NotificationConfiguration;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.ActivityTracking;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.AppActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AppActivity;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.PhysicalActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.settings.apps.AppAssignmentActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import dagger.hilt.android.AndroidEntryPoint;

import static android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED;
import static android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED;

@AndroidEntryPoint
public class ActivityTrackingService extends Service {

    @Inject
    WellbeingDatabase db;

    private SharedPreferences preferences;
    private long startTime;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // This returns immediately if the feature is disabled
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!this.preferences.getBoolean("notification_app_enabled", false)) {
            stopForeground(true);

            return super.onStartCommand(intent, flags, startId);
        }

        NotificationManager notification = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel(NotificationConfiguration.ChannelsId.CHANNEL_ID_APP_USAGE, getString(R.string.app_usage_channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        Intent appAssignmentIntent = new Intent(this, AppAssignmentActivity.class);
        PendingIntent pendingAppAssignmentIntent = PendingIntent.getActivity(this, NotificationConfiguration.RequestIds.REQUEST_CODE_APP_ASSIGNMENT, appAssignmentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationConfiguration.ChannelsId.CHANNEL_ID_APP_USAGE)
            .setContentTitle(getString(R.string.auto_tracking_foreground_notification_title))
            .setContentText(getString(R.string.auto_tracking_foreground_notification_body))
            .setColor(getColor(R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_star_logo)
            .setContentIntent(pendingAppAssignmentIntent)
            .setOnlyAlertOnce(true);

        UsageStatsManager usage = (UsageStatsManager) getSystemService(Service.USAGE_STATS_SERVICE);
        PackageManager packageManager = getPackageManager();
        startForeground(NotificationConfiguration.NotificationsId.APP_USAGE_ID, builder.build());

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            db.appActivityDao().deleteAll();
        });

        Calendar initialTime = Calendar.getInstance();
        this.startTime = initialTime.getTimeInMillis() - getDuration();

        Thread thread = new Thread(() -> {
            while (true) {
                // Stop the service if not enabled
                if (!this.preferences.getBoolean("notification_app_enabled", false)) {
                    stopForeground(true);
                    break;
                }

                int duration = getDuration();

                // Get the time now
                Calendar cal = Calendar.getInstance();
                long currentTime = cal.getTimeInMillis();

                // The difference between the last thing and now
                UsageEvents stats = usage.queryEvents(this.startTime, currentTime);

                while(stats.hasNextEvent()) {
                    UsageEvents.Event event = new UsageEvents.Event();

                    stats.getNextEvent(event);

                    AppActivityDao appUsageDao = db.appActivityDao();
                    AppActivity appActivity = appUsageDao.getPhysicalActivityByType(event.getPackageName());

                    if (appActivity == null) {
                        appActivity = new AppActivity(event.getPackageName(), 0, 0, 0, 0, false);
                        appUsageDao.insert(appActivity);
                    }

                    String packageId = event.getPackageName();

                    // Always remember the last time stamp +1 so that it can be used as the next start time to ensure no gaps
                    this.startTime = event.getTimeStamp() + 1;

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
                }

                long durationDifference = currentTime - 500;
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
                    String appName = (String) packageManager.getApplicationLabel(info);

                    // If longer than duration then need to update the physical activity details
                    if ((app.getEndTime() - app.getStartTime()) > duration) {
                        PhysicalActivity activity = db.physicalActivityDao().getPhysicalActivityByTypeWithAssociatedActivity(app.getPackageName());
                        if(activity == null) {
                            db.appActivityDao().deleteWithPackageId(packageId);
                            continue;
                        }

                        // Update the times and pending status of the activity that has just been added
                        db.physicalActivityDao().updateStartTime(packageId, app.getStartTime());
                        db.physicalActivityDao().updateEndTime(packageId, app.getEndTime());
                        db.physicalActivityDao().updateIsPendingStatus(packageId, true);

                        ActivityRecord activityData = db.activityRecordDao().getActivityRecordById(activity.getActivityId());

                        if(activityData == null) {
                            continue;
                        }

                        ActivityTracking tracking = new ActivityTracking();
                        tracking.sendActivityNotification(this, activity.getActivityId(), app.getStartTime(), app.getEndTime(), packageId, appName, activityData.getActivityName());

                        db.appActivityDao().setPending(packageId, true);
                    }

                    // Reset the record
                    db.appActivityDao().deleteWithPackageId(packageId);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
            return START_STICKY;
    }

    private int getDuration() {
        int duration = this.preferences.getInt("notification_app_duration", 10);
        return (duration*60000);
    }


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