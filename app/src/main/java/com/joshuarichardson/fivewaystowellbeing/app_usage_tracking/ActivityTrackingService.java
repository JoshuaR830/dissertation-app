package com.joshuarichardson.fivewaystowellbeing.app_usage_tracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.ActivityTracking;
import com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.AutomaticActivityTypes;
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
import static com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.AutomaticActivityTypes.APP;

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

                Log.d("Start time", String.valueOf(startTime));
                Log.d("End time", String.valueOf(currentTime));

                List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, currentTime);
                UsageEvents stats2 = usage.queryEvents(startTime, currentTime);

                while(stats2.hasNextEvent()) {


                    UsageEvents.Event event = new UsageEvents.Event();

                    stats2.getNextEvent(event);

                    ApplicationInfo info;
                    try {
                        info = packageManager.getApplicationInfo(event.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        continue;
                    }
                    String name = (String) packageManager.getApplicationLabel(info);

                    Log.d("Name", event.getPackageName());
                    Log.d("Type", String.valueOf(event.getEventType()));

                    WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                        AppActivityDao appUsageDao = db.appActivityDao();
                        AppActivity appActivity = appUsageDao.getPhysicalActivityByType(event.getPackageName());

                        if (appActivity == null) {
                            appActivity = new AppActivity(event.getPackageName(), 0, 0, 0, 0, false);
                            appUsageDao.insert(appActivity);
                        }

                        String packageId = event.getPackageName();

                        // ToDo If there is a pause and a stop but no resume then assume death

                        // Get the timestamp when the activity started
                        if (event.getEventType() == ACTIVITY_RESUMED) {
                            Log.d("RESUMED", name + event.getTimeStamp());
                            Log.d("RESUMED", String.valueOf(appActivity.getStartTime()));
                            if (appActivity.getStartTime() == 0) {
                                // The first thing that happens in an app is it resumes so ideal to update the start time and resumed time
                                appUsageDao.updateStartAndLastResumedTime(packageId, event.getTimeStamp(), event.getTimeStamp());
                            } else {
                                appUsageDao.updateUsageTime(packageId, event.getTimeStamp());
                            }
                        }

                        // Get the timestamp when the activity paused
                        if (event.getEventType() == ACTIVITY_PAUSED) {
                            Log.d("PAUSED", name);
                            // The last thing that happens in an app is that it pauses
                            appUsageDao.updateEndTime(packageId, event.getTimeStamp());
                        }

                                // If longer than duration then need to update the physical activity details
                                if (usageDifference > DURATION_THRESHOLD) {
                                    // This is already wired up to display things and stuff so makes sense to reuse
                                    db.physicalActivityDao().insert(new PhysicalActivity(packageId, 0, 0, 1, false, false));

                            // Update the times and pending status of the activity that has just been added
                            db.physicalActivityDao().updateStartTime(packageId, app.getStartTime());
                            db.physicalActivityDao().updateEndTime(packageId, app.getEndTime());
                            db.physicalActivityDao().updateIsPendingStatus(packageId, true);

                            // ToDo send a notification to the user telling them
//                            NotificationCompat.Builder appUsageNotificationBuilder = new NotificationCompat.Builder(this, APP_TRACKING_CHANNEL_ID)
//                                .setContentTitle("Add " + name + " to your log")
//                                .setContentText(convertToHoursMinutesSeconds(app.getEndTime() - app.getStartTime()))
//                                .setSmallIcon(R.drawable.ic_star_logo);

//                            notification.notify(201, appUsageNotificationBuilder.build());



                            ActivityTracking tracking = new ActivityTracking();
                            tracking.sendActivityNotification(this, activity.getActivityId(), activity.getStartTime(), activity.getEndTime(), packageId, name);


                            db.appActivityDao().setPending(packageId, true);
                        }

                        // Reset the record
                        db.appActivityDao().deleteWithPackageId(packageId);
                    }
                });

//                int counter = 0;
//                counter ++;
//                Log.d("Count", String.valueOf(counter));
//                for (UsageStats stat : stats) {
//                    if (stat.getTotalTimeInForeground() > 0) {
//
//                        // ToDo - packages are unique - so can have them as a key
//                        // ToDo - need to query DB for the package name - had it started previously (is null then it hadn't)
//                        // ToDo - need to make it pending
//
//                        ApplicationInfo info;
//                        try {
//                            info = packageManager.getApplicationInfo(stat.getPackageName(), 0);
//                        } catch (PackageManager.NameNotFoundException e) {
//                            continue;
//                        }
//
//                        String name = (String) packageManager.getApplicationLabel(info);
//                        if (name == null) {
//                            Log.d(stat.getPackageName(), convertToHoursMinutes(stat.getTotalTimeInForeground()));
//                            continue;
//                        }
//
//                        // Need a database with a, package, start time, end time, is_pending - basically the physical activity tracking
//                        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
//                            AppActivityDao appUsageDao = db.appActivityDao();
//                            AppActivity appActivity = appUsageDao.getPhysicalActivityByType(stat.getPackageName());
//
//                            appActivity = appUsageDao.getAppActivityByTypeNotPending(stat.getPackageName());
//
//                            if (appActivity == null) {
//                                Log.d("App Activity", "Null");
//                                appActivity = new AppActivity(stat.getPackageName(), currentTime, stat.getTotalTimeInForeground(), stat.getTotalTimeInForeground(), false);
//                            }
//
//                            // Get the difference between when this was logged and the current time
//                            long usageDifference = stat.getTotalTimeInForeground() - appActivity.getPreviousUsageTime();
//                            long serviceStartTime = currentTime - usageDifference;
//
//                            String packageId = stat.getPackageName();
//
//                            // The current usage time needs to be greater than the previous usage time
//                            if (stat.getTotalTimeInForeground() > appActivity.getPreviousUsageTime()) {
//                                if (appActivity.getStartTime() == 0) {
//                                    appUsageDao.updateStartAndUsageTime(packageId, serviceStartTime, stat.getTotalTimeInForeground());
//                                }
//
//                                // Best guess at finish time based on start time + usage difference though doesn't account for multiple sessions in that time
////                        appUsageDao.updateEndTime(packageId, serviceStartTime + usageDifference);
//                            }
//
//                            if(usageDifference > 0) {
//                                Log.d("Usage difference", String.format("%s %d", name, usageDifference));
//                            }
//
//                            // This checks if there were any changes in the period of time stated
//                            if (stat.getTotalTimeInForeground() == appActivity.getCurrentUsage() && appActivity.getStartTime() != 0) {
//
//                                // If longer than duration then need to update the physical activity details
//                                if (usageDifference > DURATION_THRESHOLD) {
//                                    // This is already wired up to display things and stuff so makes sense to reuse
//                                    db.physicalActivityDao().insert(new PhysicalActivity(packageId, 0, 0, 1, false));
//
//                                    // Update the times and pending status of the activity that has just been added
//                                    db.physicalActivityDao().updateStartTime(packageId, appActivity.getStartTime());
//                                    db.physicalActivityDao().updateEndTime(packageId, appActivity.getStartTime() + usageDifference);
//                                    db.physicalActivityDao().updateIsPendingStatus(packageId, true);
//                                }
//
//                                // This will hide it but keep the info
//                                appUsageDao.setPending(packageId, true);
//
//                                // This will create the next item without a start time but with the current foreground time as this will be crucial to know if it has updated
//                                // Name, start time, endTime, previousTIme, currentTime, isPending
//                                appUsageDao.insert(new AppActivity(packageId, 0, 0, 0, false));
//                            }
//
//                        });
//
//                        Log.d(name, convertToHoursMinutes(stat.getTotalTimeInForeground()));
//                    }
//
//                }
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
        }; private String convertToHoursMinutesSeconds(long millis) {
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