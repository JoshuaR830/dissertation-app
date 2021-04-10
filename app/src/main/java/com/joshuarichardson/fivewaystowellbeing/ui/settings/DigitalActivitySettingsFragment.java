package com.joshuarichardson.fivewaystowellbeing.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.app_usage_tracking.ActivityTrackingService;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class DigitalActivitySettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.digital_activity_settings, rootKey);

        Preference appEnabled = findPreference("notification_app_enabled");

        appEnabled.setOnPreferenceChangeListener((preference, newValue) -> {
            if(newValue == Boolean.valueOf(String.valueOf(newValue))) {
                requireContext().startService(new Intent(requireContext(), ActivityTrackingService.class));
            }

            return true;
        });
    }
}
