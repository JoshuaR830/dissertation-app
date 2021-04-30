package com.joshuarichardson.fivewaystowellbeing.ui.settings.notifications;

import android.content.SharedPreferences;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.Locale;

import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
/**
 * Helper for setting up the settings
 */
public class NotificationSettingsHelper {
    /**
     * Allow users to set the time for the notification.
     * Update the summary.
     *
     * @param preferenceToSet The setting to change
     * @param timeOfDay The time of day (morning, noon or night)
     * @param fragmentManager Reference to the fragment manager
     * @param preferences Reference to the shared preferences
     */
    public static void setUpSettings(final Preference preferenceToSet, final String timeOfDay, final FragmentManager fragmentManager, final SharedPreferences preferences){

        final String notificationKey = "notification_" + timeOfDay;

        // Get the time for any of the notifications in any slot for the day
        int hours = (int) (preferenceToSet.getSharedPreferences().getLong(notificationKey, 0) / 1000 / 60 / 60);
        int minutes = (int) (preferenceToSet.getSharedPreferences().getLong(notificationKey, 0) / 1000 / 60);
        minutes -= hours * 60;

        preferenceToSet.setSummary(String.format(Locale.getDefault(), "Time %02d:%02d", hours, minutes));

        preferenceToSet.setOnPreferenceClickListener(preference -> {

            // Every time the preference is clicked, get the latest time
            int localHours = (int) (preferenceToSet.getSharedPreferences().getLong(notificationKey, 0) / 1000 / 60 / 60);
            int localMinutes = (int) (preferenceToSet.getSharedPreferences().getLong(notificationKey, 0) / 1000 / 60);

            // Create a time picker that will display the time for users to pick
            final MaterialTimePicker startTimePicker = new MaterialTimePicker.Builder()
                .setTitleText(R.string.notification_time_title)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(localHours)
                .setMinute(localMinutes)
                .build();

            startTimePicker.show(fragmentManager, "start");

            // When a time is selected handle it
            startTimePicker.addOnPositiveButtonClickListener(v -> {
                int hour = startTimePicker.getHour();
                int minute = startTimePicker.getMinute();

                long ms = hour * 60 * 60 * 1000;
                ms += minute * 60 * 1000;

                // Update the preferences
                SharedPreferences.Editor prefEdit = preferences.edit();
                prefEdit.putLong(notificationKey, ms);
                prefEdit.apply();
                preference.setSummary(String.format(Locale.getDefault(), "Time %02d:%02d", hour, minute));
            });

            return true;
        });
    }
}
