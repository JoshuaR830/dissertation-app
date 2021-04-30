package com.joshuarichardson.fivewaystowellbeing.ui.settings.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * Fragment to show night settings
 */
public class NightSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.night_settings, rootKey);

        Preference night = findPreference("notification_night_time");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        NotificationSettingsHelper.setUpSettings(night, "night_time", getParentFragmentManager(), preferences);
    }
}
