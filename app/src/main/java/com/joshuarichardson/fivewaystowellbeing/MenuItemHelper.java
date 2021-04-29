package com.joshuarichardson.fivewaystowellbeing;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.joshuarichardson.fivewaystowellbeing.ui.settings.SettingsActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.history.AddMissedDayActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support.WellbeingSupportActivity;

public class MenuItemHelper {
    /** Return the intent of an activity to launch based on overflow menu item selected
     * @param activity The activity from which the intent was launched
     * @param item The menu item id that was clicked
     * @return The intent of the activity which should be launched
     */
    public static Intent handleOverflowMenuClick(Activity activity, MenuItem item) {

        // Return intent of activity to launch
        if (item.getItemId() == R.id.menu_settings) {
            return new Intent(activity, SettingsActivity.class);
        } else if (item.getItemId() == R.id.menu_wellbeing_support) {
            return new Intent(activity, WellbeingSupportActivity.class);
        } else if (item.getItemId() == R.id.menu_learn_more) {
            return new Intent(activity, LearnMoreAboutFiveWaysActivity.class);
        } else if (item.getItemId() == R.id.menu_missing_day) {
            return new Intent(activity, AddMissedDayActivity.class);
        } else {
            return null;
        }
    }
}
