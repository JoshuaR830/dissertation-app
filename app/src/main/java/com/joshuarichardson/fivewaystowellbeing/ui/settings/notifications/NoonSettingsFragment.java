package com.joshuarichardson.fivewaystowellbeing.ui.settings.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class NoonSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.noon_settings, rootKey);

        Preference noon = findPreference("notification_noon_time");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        NotificationSettingsHelper.setUpSettings(noon, "noon_time", getParentFragmentManager(), preferences);
    }
}
