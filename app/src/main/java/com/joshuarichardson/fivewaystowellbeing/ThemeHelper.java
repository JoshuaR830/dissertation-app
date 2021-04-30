package com.joshuarichardson.fivewaystowellbeing;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Set the application theme to the selected theme or default theme if not set by the user
 */
public class ThemeHelper {
    /**
     * Set the application colour theme
     *
     * @param theme The theme preference
     */
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
