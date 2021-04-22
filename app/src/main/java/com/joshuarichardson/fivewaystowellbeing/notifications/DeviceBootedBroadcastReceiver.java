package com.joshuarichardson.fivewaystowellbeing.notifications;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.app_usage_tracking.ActivityTrackingService;
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking.ActivityTracking;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

// References:
// https://stackoverflow.com/questions/12034357/does-alarm-manager-persist-even-after-reboot
public class DeviceBootedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmHelper helper = AlarmHelper.getInstance();

        // Schedule all of the alarms for their correct time
        helper.scheduleNotification(context, "morning");
        helper.scheduleNotification(context, "noon");
        helper.scheduleNotification(context, "night");

        ActivityTracking activityTracker = new ActivityTracking();
        // This will start the tracking for the activities
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check that permissions have been granted for devices that require it
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                activityTracker.initialiseTracking(context);
            }
        } else {
            activityTracker.initialiseTracking(context);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getBoolean("notification_app_enabled", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Reference startForegroundService https://stackoverflow.com/a/7690600/13496270
                context.startForegroundService(new Intent(context, ActivityTrackingService.class));
            } else {
                context.startService(new Intent(context, ActivityTrackingService.class));
            }
        }
    }
}
