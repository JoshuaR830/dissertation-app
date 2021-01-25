package com.joshuarichardson.fivewaystowellbeing.ui.settings;

import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.ThemeHelper;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

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

    }
}
