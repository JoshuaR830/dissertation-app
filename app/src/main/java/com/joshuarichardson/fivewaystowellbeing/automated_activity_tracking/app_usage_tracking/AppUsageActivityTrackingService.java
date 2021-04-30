package com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.app_usage_tracking;

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
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.AutomaticNotificationHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.AppActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AppActivity;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AutomaticActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.settings.apps.AppAssignmentActivity;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import dagger.hilt.android.AndroidEntryPoint;

import static android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED;
import static android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED;

/**
 * A service to track application usage for selected apps
 */
@AndroidEntryPoint
public class AppUsageActivityTrackingService extends Service {

    @Inject
    WellbeingDatabase db;

    @Inject
    AutomaticNotificationHelper automaticNotificationHelper;

    private SharedPreferences preferences;
    private long startTime;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // This ends immediately if the feature is disabled
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!this.preferences.getBoolean("notification_app_enabled", false)) {
            stopForeground(true);
            return super.onStartCommand(intent, flags, startId);
        }

        startServiceAsForeground();

        UsageStatsManager usageManager = (UsageStatsManager) getSystemService(Service.USAGE_STATS_SERVICE);
        PackageManager packageManager = getPackageManager();

        // Reset the tracking table when the service starts to avoid false positives
        WellbeingDatabaseModule.databaseExecutor.execute(() -> db.appActivityDao().deleteAll());

        this.startTime = Calendar.getInstance().getTimeInMillis() - getMinimumDuration();

        Thread thread = new Thread(() -> {
            while (true) {
                // Stop the service if not enabled
                if (!this.preferences.getBoolean("notification_app_enabled", false)) {
                    stopForeground(true);
                    break;
                }

                long currentTime = Calendar.getInstance().getTimeInMillis();

                processUsageStats(usageManager, currentTime);
                processFinishedApps(packageManager, currentTime);

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

    /**
     * Create the persistent notification and run the foreground service
     */
    private void startServiceAsForeground() {
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

        startForeground(NotificationConfiguration.NotificationsId.APP_USAGE_ID, builder.build());
    }

    /**
     * Process the usage stats between specific times
     * Update the app usage table to contain usage time difference
     *
     * @param usageManager An instance of the UsageStatsManager
     * @param currentTime The current time in milliseconds
     */
    private void processUsageStats(UsageStatsManager usageManager, long currentTime) {
        // The difference between the last thing and now
        UsageEvents stats = usageManager.queryEvents(this.startTime, currentTime);

        while(stats.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();

            stats.getNextEvent(event);

            AppActivityDao appUsageDao = db.appActivityDao();
            AppActivity appActivity = appUsageDao.getPhysicalActivityByType(event.getPackageName());

            if (appActivity == null) {
                appActivity = new AppActivity(event.getPackageName(), 0, 0, 0, false);
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
    }

    /**
     * Get all finished activities and process them to calculate whether they are long enough to log.
     * Users will be notified about any application they have used for long enough to log.
     *
     * @param packageManager An instance of the PackageManager to allow information about the package to be retrieved
     * @param currentTime The current time in milliseconds
     */
    public void processFinishedApps(PackageManager packageManager, long currentTime) {
        long minimumDuration = getMinimumDuration();
        long durationDifference = currentTime - 500;

        // Get a list of all the activities that are finished since last time
        List<AppActivity> finishedApps = db.appActivityDao().getFinishedApplications(durationDifference);

        for(AppActivity app : finishedApps) {
            String packageId = app.getPackageName();

            String appName = getAppNameForPackage(packageManager, packageId);

            if(appName == null) {
                continue;
            }

            // If longer than duration then need to update the physical activity details
            if ((app.getEndTime() - app.getStartTime()) > minimumDuration) {
                AutomaticActivity activity = db.physicalActivityDao().getPhysicalActivityByTypeWithAssociatedActivity(app.getPackageName());

                // Remove the record of the activity if it is not one to log
                if(activity == null) {
                    db.appActivityDao().deleteWithPackageId(packageId);
                    continue;
                }

                // Update the times and pending status of the activity that has just been added
                db.physicalActivityDao().updateStartTime(packageId, app.getStartTime());
                db.physicalActivityDao().updateEndTime(packageId, app.getEndTime());
                db.physicalActivityDao().updateIsPendingStatus(packageId, true);

                // Get the activity data associated with the activity that users selected to log
                ActivityRecord activityData = db.activityRecordDao().getActivityRecordById(activity.getActivityId());

                if(activityData == null) {
                    continue;
                }

                this.automaticNotificationHelper.sendSuggestedActivityNotification(activity.getActivityId(), app.getStartTime(), app.getEndTime(), packageId, appName, activityData.getActivityName());

                db.appActivityDao().setPending(packageId, true);
            }

            // Reset the record
            db.appActivityDao().deleteWithPackageId(packageId);
        }
    }

    /**
     * Get the name of the application based on the package name
     *
     * @param packageManager An instance of the package manager
     * @param packageId The identifier for the package
     * @return The name of the package if the package exists
     */
    private String getAppNameForPackage(PackageManager packageManager, String packageId) {
        ApplicationInfo info;
        try {
            info = packageManager.getApplicationInfo(packageId, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        return (String) packageManager.getApplicationLabel(info);
    }

    /**
     * Get the selected duration for app usage as defined in settings
     *
     * @return The minimum number of milliseconds defined by the user
     */
    private int getMinimumDuration() {
        int duration = this.preferences.getInt("notification_app_duration", 10);
        return (duration*60000);
    }

    @Override
    // The program never binds to this service - it runs as a started service
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}