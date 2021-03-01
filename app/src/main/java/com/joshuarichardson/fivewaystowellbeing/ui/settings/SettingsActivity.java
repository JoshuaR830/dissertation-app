package com.joshuarichardson.fivewaystowellbeing.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.notifications.AlarmHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private SharedPreferences preferences;

    SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        int hours;
        int minutes;

        String timeOfDay;

        // Set defaults if necessary based on the changed setting
        switch (key) {
            case "notification_morning_switch":
            case "notification_morning_time":
                timeOfDay = "morning";
                hours = 8;
                minutes = 30;
                break;
            case "notification_noon_switch":
            case "notification_noon_time":
                timeOfDay = "noon";
                hours = 12;
                minutes = 0;
                break;
            case "notification_night_switch":
            case "notification_night_time":
                timeOfDay ="night";
                hours = 17;
                minutes = 49;
                break;
            default:
                return;
        }

        // Attempt to get the real settings
        boolean isEnabled = sharedPreferences.getBoolean("notification_" + timeOfDay + "_switch", true);
        long time = sharedPreferences.getLong("notification_" + timeOfDay + "_time", -1);

        if(time == -1) {
            return;
        }

        if(time > 0) {
            hours = (int) time / 60 / 60 / 1000;
            minutes = (int) (time / 60 / 1000) - (hours * 60);
        }

        AlarmHelper.getInstance().scheduleNotification(getApplicationContext(), hours, minutes, timeOfDay, isEnabled);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//         Reference: https://developer.android.com/guide/topics/ui/settings#java
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.wellbeing_settings_container , new WellbeingSettingsFragment())
            .commit();

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    // Reference: https://developer.android.com/guide/topics/ui/settings/organize-your-settings#split_your_hierarchy_into_multiple_screens
    // This starts a new fragment for settings when user selects a setting with a new screen
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {

        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
            getClassLoader(),
            pref.getFragment()
        );

        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);

        getSupportFragmentManager().beginTransaction().replace(R.id.wellbeing_settings_container, fragment)
            .addToBackStack(null)
            .commit();

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.preferences.registerOnSharedPreferenceChangeListener(this.listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.preferences.unregisterOnSharedPreferenceChangeListener(this.listener);
    }
}