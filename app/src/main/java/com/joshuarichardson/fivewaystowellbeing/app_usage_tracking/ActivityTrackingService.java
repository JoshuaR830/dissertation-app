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

@AndroidEntryPoint
public class ActivityTrackingService extends Service {

    @Inject
    WellbeingDatabase db;

    private final int DURATION_THRESHOLD = 60000;
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

        Thread thread = new Thread(() -> {
            while (true) {
                Calendar cal = Calendar.getInstance();
                long currentTime = cal.getTimeInMillis();
                cal.add(Calendar.MINUTE, -DURATION_THRESHOLD);
                long startTime = cal.getTimeInMillis();

                Log.d("Start time", String.valueOf(startTime));
                Log.d("End time", String.valueOf(currentTime));

                List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, currentTime);
                UsageEvents stats2 = usage.queryEvents(startTime, currentTime);

                while(stats2.hasNextEvent()) {
                    UsageEvents.Event event = null;

                    stats2.getNextEvent(event);

                    Log.d("Name", event.getPackageName());
                    Log.d("Name", String.valueOf(event.getEventType()));
                }

                int counter = 0;
                counter ++;
                Log.d("Count", String.valueOf(counter));
                for (UsageStats stat : stats) {
                    if (stat.getTotalTimeInForeground() > 0) {

                        // ToDo - packages are unique - so can have them as a key
                        // ToDo - need to query DB for the package name - had it started previously (is null then it hadn't)
                        // ToDo - need to make it pending

                        ApplicationInfo info;
                        try {
                            info = packageManager.getApplicationInfo(stat.getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            continue;
                        }

                        String name = (String) packageManager.getApplicationLabel(info);
                        if (name == null) {
                            Log.d(stat.getPackageName(), convertToHoursMinutes(stat.getTotalTimeInForeground()));
                            continue;
                        }

                        // Need a database with a, package, start time, end time, is_pending - basically the physical activity tracking
                        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                            AppActivityDao appUsageDao = db.appActivityDao();
                            AppActivity appActivity = appUsageDao.getPhysicalActivityByType(stat.getPackageName());

                            appActivity = appUsageDao.getAppActivityByTypeNotPending(stat.getPackageName());

                            if (appActivity == null) {
                                Log.d("App Activity", "Null");
                                appActivity = new AppActivity(stat.getPackageName(), currentTime, stat.getTotalTimeInForeground(), stat.getTotalTimeInForeground(), false);
                            }

                            // Get the difference between when this was logged and the current time
                            long usageDifference = stat.getTotalTimeInForeground() - appActivity.getPreviousUsageTime();
                            long serviceStartTime = currentTime - usageDifference;

                            String packageId = stat.getPackageName();

                            // The current usage time needs to be greater than the previous usage time
                            if (stat.getTotalTimeInForeground() > appActivity.getPreviousUsageTime()) {
                                if (appActivity.getStartTime() == 0) {
                                    appUsageDao.updateStartAndUsageTime(packageId, serviceStartTime, stat.getTotalTimeInForeground());
                                }

                                // Best guess at finish time based on start time + usage difference though doesn't account for multiple sessions in that time
//                        appUsageDao.updateEndTime(packageId, serviceStartTime + usageDifference);
                            }

                            if(usageDifference > 0) {
                                Log.d("Usage difference", String.format("%s %d", name, usageDifference));
                            }

                            // This checks if there were any changes in the period of time stated
                            if (stat.getTotalTimeInForeground() == appActivity.getCurrentUsage() && appActivity.getStartTime() != 0) {

                                // If longer than duration then need to update the physical activity details
                                if (usageDifference > DURATION_THRESHOLD) {
                                    // This is already wired up to display things and stuff so makes sense to reuse
                                    db.physicalActivityDao().insert(new PhysicalActivity(packageId, 0, 0, 1, false, false));

                                    // Update the times and pending status of the activity that has just been added
                                    db.physicalActivityDao().updateStartTime(packageId, appActivity.getStartTime());
                                    db.physicalActivityDao().updateEndTime(packageId, appActivity.getStartTime() + usageDifference);
                                    db.physicalActivityDao().updateIsPendingStatus(packageId, true);
                                }

                                // This will hide it but keep the info
                                appUsageDao.setPending(packageId, true);

                                // This will create the next item without a start time but with the current foreground time as this will be crucial to know if it has updated
                                // Name, start time, endTime, previousTIme, currentTime, isPending
                                appUsageDao.insert(new AppActivity(packageId, 0, 0, 0, false));
                            }

                        });

//
//                    if (appActivity == null) {
//
//                        // ToDo not total time in foreground - rather the difference between previous and this
//                        long serviceStartTime = currentTime - stat.getTotalTimeInForeground();
////                    currentTime;
////                        appActivity = new PhysicalActivity(stat.getPackageName(), serviceStartTime, currentTime, 1, true);
//                        appUsageDao.insert(appActivity);
//                    } else {
//                        long time = appActivity.getEndTime() - appActivity.getStartTime();
//                        if(stat.getTotalTimeInForeground() == time) {
//                            // This means the time hasn't changed since the last update
//                            // ToDo - Need to do something so that next time it changes it is a new activity
//                            // ToDo - ignore collisions - so can't add new items
//                            return;
//                        }


                        // If time has changed - assume it is by current - that doesn't really work though
                        // Should probably not assume that all the minutes have passed
                        // Should check time status on current time then check

                        //ToDo endtime is 0 only if not updated

                        // ToDo - the difference between start and end should be 10 minutes before it does anything to change the data
                        // ToDo - once threshold crossed - magic is real - just keep logging
//                        if(appActivity.getTimeStatus(currentTime)) {
//                            appUsageDao.updateEndTime(stat.getPackageName(), currentTime);
//                        }
//
//                        if (appActivity.getTimeStatus(currentTime)) {
//                            Log.d("Something" , "It's true");
//                        }
//                    }


                        Log.d(name, convertToHoursMinutes(stat.getTotalTimeInForeground()));
                    }

                }
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
        }; private String convertToHoursMinutes(long millis) {
        long minutes = millis / 1000 / 60;
        long hours = minutes / 60;
        minutes -= hours * 60;

        return String.format(Locale.getDefault(), "%dh %dm", hours, minutes);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}