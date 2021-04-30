package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.content.SharedPreferences;

import com.joshuarichardson.fivewaystowellbeing.notifications.AlarmHelper;

import javax.inject.Inject;

import androidx.preference.PreferenceManager;
import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Set up the initial preferences when the app is first launched
 */
public class InitialPreferencesHelper {

    Context context;

    private final SharedPreferences preferences;

    @Inject
    InitialPreferencesHelper(@ApplicationContext Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Schedule notifications for the app if the user hasn't already set times
     *
     * @param alarmHelper An instance of the alarm helper for scheduling notifications
     */
    public void setInitialNotificationPreferences(AlarmHelper alarmHelper) {

        SharedPreferences.Editor preferenceEditor = preferences.edit();
        // Schedule default notification and set values if not set
        if (!preferences.contains("notification_morning_time") || !preferences.contains("notification_morning_switch")) {
            preferenceEditor.putLong("notification_morning_time", 30600000); // 8:30
            preferenceEditor.putBoolean("notification_morning_switch", true);
            alarmHelper.scheduleNotification(context, 8, 30, "morning", true);
        }

        if (!preferences.contains("notification_noon_time") || !preferences.contains("notification_noon_switch")) {
            preferenceEditor.putLong("notification_noon_time", 43200000); // 12:00
            preferenceEditor.putBoolean("notification_noon_switch", true);
            alarmHelper.scheduleNotification(context, 12, 0, "noon", true);
        }

        if (!preferences.contains("notification_night_time") || !preferences.contains("notification_night_switch")) {
            preferenceEditor.putLong("notification_night_time", 73800000); // 20:30
            preferenceEditor.putBoolean("notification_night_switch", true);
            alarmHelper.scheduleNotification(context, 20, 30, "night", true);
        }

        preferenceEditor.apply();
    }

    /**
     * Set the default toggle status and durations for physical activities.
     */
    public void setInitialPhysicalActivitySettings() {
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        if (! preferences.contains("notification_walk_enabled")) {
            preferenceEditor.putBoolean("notification_walk_enabled", true);
        }

        if (!preferences.contains("notification_walk_duration")) {
            preferenceEditor.putInt("notification_walk_duration", 10);
        }

        if (! preferences.contains("notification_run_enabled")) {
            preferenceEditor.putBoolean("notification_run_enabled", true);
        }

        if (!preferences.contains("notification_run_duration")) {
            preferenceEditor.putInt("notification_run_duration", 10);
        }

        if (! preferences.contains("notification_cycle_enabled")) {
            preferenceEditor.putBoolean("notification_cycle_enabled", true);
        }

        if (!preferences.contains("notification_cycle_duration")) {
            preferenceEditor.putInt("notification_cycle_duration", 10);
        }

        if (! preferences.contains("notification_drive_enabled")) {
            preferenceEditor.putBoolean("notification_drive_enabled", false);
        }

        if (!preferences.contains("notification_drive_duration")) {
            preferenceEditor.putInt("notification_drive_duration", 30);
        }

        if (! preferences.contains("notification_app_enabled")) {
            preferenceEditor.putBoolean("notification_app_enabled", false);
        }

        if (!preferences.contains("notification_app_duration")) {
            preferenceEditor.putInt("notification_app_duration", 10);
        }

        preferenceEditor.apply();
    }
}
