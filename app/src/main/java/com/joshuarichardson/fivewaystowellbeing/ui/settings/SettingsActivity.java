package com.joshuarichardson.fivewaystowellbeing.ui.settings;

import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//         Reference: https://developer.android.com/guide/topics/ui/settings#java
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.wellbeing_settings_container , new WellbeingSettingsFragment())
            .commit();
    }
}