package com.joshuarichardson.fivewaystowellbeing;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    public static void setTheme(String theme) {
        // Set the theme of the app based on the chosen setting
        switch (theme) {
            case "DARK":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "LIGHT":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
