package com.joshuarichardson.fivewaystowellbeing.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.ThemeHelper;
import com.joshuarichardson.fivewaystowellbeing.ui.settings.notifications.NotificationSettingsHelper;

import java.util.Locale;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class WellbeingSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.wellbeing_settings, rootKey);

        ListPreference themePreference = findPreference("theme_settings_list");
        if(themePreference == null) {
            return;
        }

        themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            ThemeHelper.setTheme(newValue.toString());
            return true;
        });

        Preference morning = findPreference("notification_morning");
        Preference noon = findPreference("notification_noon");
        Preference night = findPreference("notification_night");

        // If they don't exist stop the code
        if(morning == null || noon == null || night == null) {
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        NotificationSettingsHelper.setUpSettings(morning, "morning", getParentFragmentManager(), preferences);
        NotificationSettingsHelper.setUpSettings(noon, "noon", getParentFragmentManager(), preferences);
        NotificationSettingsHelper.setUpSettings(night, "night", getParentFragmentManager(), preferences);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Ensure the page summary gets updated whenever the page is resumed
        setPageSummary("morning");
        setPageSummary("noon");
        setPageSummary("night");
    }

    private void setPageSummary(final String timeOfDay) {
        String notificationSwitchKey = "notification_" + timeOfDay + "_switch";
        String notificationPageKey = "notification_" + timeOfDay + "_page";

        Preference pagePref = findPreference(notificationPageKey);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isEnabled = defaultSharedPreferences.getBoolean(notificationSwitchKey, true);

        String stateText = (isEnabled ? getString(R.string.enabled) : getString(R.string.disabled));

        // If disabled - hide the time - because time implies that you will get notification at that time
        if(isEnabled) {
            String notificationTimeKey = "notification_" + timeOfDay + "_time";
            long time = defaultSharedPreferences.getLong(notificationTimeKey, 0);
            int hours = (int) (time / 60 / 60 / 1000);
            int minutes = (int) (time / 60 / 1000) - (hours * 60);
            pagePref.setSummary(String.format(Locale.getDefault(), "%s - %02d:%02d", stateText, hours, minutes));
        } else {
            pagePref.setSummary(stateText);
        }
    }
}
