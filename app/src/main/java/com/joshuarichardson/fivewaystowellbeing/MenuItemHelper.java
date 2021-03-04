package com.joshuarichardson.fivewaystowellbeing;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.joshuarichardson.fivewaystowellbeing.ui.settings.SettingsActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support.WellbeingSupportActivity;

public class MenuItemHelper {
    public static Intent handleMenuClick(Activity activity, MenuItem item) {
        switch(item.getItemId()) {
            // I want to launch activities
            case R.id.menu_settings:
                return new Intent(activity, SettingsActivity.class);
            case R.id.menu_wellbeing_support:
                return new Intent(activity, WellbeingSupportActivity.class);
            case R.id.menu_learn_more:
                return new Intent(activity, LearnMoreAboutFiveWaysActivity.class);
            default:
                return null;
        }
    }
}
