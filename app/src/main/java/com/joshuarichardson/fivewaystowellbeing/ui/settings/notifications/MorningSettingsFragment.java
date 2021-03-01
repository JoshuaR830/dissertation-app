package com.joshuarichardson.fivewaystowellbeing.ui.settings.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class MorningSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.morning_settings, rootKey);

        Preference morning = findPreference("notification_morning_time");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        NotificationSettingsHelper.setUpSettings(morning, "morning_time", getParentFragmentManager(), preferences);
    }
}
