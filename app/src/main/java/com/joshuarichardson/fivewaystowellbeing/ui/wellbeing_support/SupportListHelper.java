package com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.ArrayList;

/**
 * Provides a list of support items to display to users
 */
public class SupportListHelper {

    private static ArrayList<WellbeingSupportItem> supportList;

    /**
     * Return a list of all support websites that are available
     *
     * @param context The application context
     * @return A list of wellbeing support items
     */
    public static ArrayList<WellbeingSupportItem> getList(Context context) {
        if(supportList == null) {
            supportList = new ArrayList<>();
            supportList.add(new WellbeingSupportItem(context.getString(R.string.mind_title), context.getString(R.string.mind_description), R.drawable.help_icon_mind, "https://www.mind.org.uk/information-support/tips-for-everyday-living/"));
            supportList.add(new WellbeingSupportItem(context.getString(R.string.nhs_title), context.getString(R.string.nhs_description), R.drawable.help_icon_nhs, "https://www.nhs.uk/using-the-nhs/nhs-services/mental-health-services/"));
            supportList.add(new WellbeingSupportItem(context.getString(R.string.resources_title), context.getString(R.string.resources_description), R.drawable.help_icon_self_help, "https://www.annafreud.org/on-my-mind/self-care/"));
        }

        return supportList;
    }
}
