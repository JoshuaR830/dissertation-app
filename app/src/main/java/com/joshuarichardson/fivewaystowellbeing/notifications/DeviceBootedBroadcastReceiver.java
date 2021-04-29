package com.joshuarichardson.fivewaystowellbeing.notifications;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.app_usage_tracking.AppUsageActivityTrackingService;
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking.PhysicalActivityTracking;

import javax.inject.Inject;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * When the device boots schedule notifications and start the tracking service
 *
 * References: https://stackoverflow.com/questions/12034357/does-alarm-manager-persist-even-after-reboot
 */
@AndroidEntryPoint
public class DeviceBootedBroadcastReceiver extends BroadcastReceiver {

    @Inject
    PhysicalActivityTracking activityTracker;

    @Inject
    AlarmHelper alarmHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            scheduleNotifications(context);
            startPhysicalTracking(context);
            startAppTrackingService(context);
        }
    }

    /**
     * Schedule all of the alarms for their correct time
     *
     * @param context The application context
     */
    private void scheduleNotifications(Context context) {
        this.alarmHelper.scheduleNotification(context, "morning");
        this.alarmHelper.scheduleNotification(context, "noon");
        this.alarmHelper.scheduleNotification(context, "night");
    }

    /**
     * This will start the tracking for the physical activities
     * The permission must be enabled on Android Q+
     *
     * @param context The application context
     */
    private void startPhysicalTracking(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check that permissions have been granted for devices that require it
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                this.activityTracker.initialiseTracking(context);
            }
        } else {
            this.activityTracker.initialiseTracking(context);
        }
    }

    /**
     * This will start the activity tracking service if it is enabled in application settings
     *
     * @param context The application context
     */
    private void startAppTrackingService(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getBoolean("notification_app_enabled", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Reference startForegroundService https://stackoverflow.com/a/7690600/13496270
                context.startForegroundService(new Intent(context, AppUsageActivityTrackingService.class));
            } else {
                context.startService(new Intent(context, AppUsageActivityTrackingService.class));
            }
        }
    }


}
