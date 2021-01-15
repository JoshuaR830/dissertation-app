package com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support;

import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.ArrayList;

public class SupportListHelper {

    private static ArrayList<WellbeingSupportItem> supportList;

    public static ArrayList<WellbeingSupportItem> getList() {
        if(supportList == null) {
            supportList = new ArrayList<>();
            supportList.add(new WellbeingSupportItem("Mind", "Tips and guides for managing with mental health problems.", R.drawable.help_icon_mind, "https://www.mind.org.uk/information-support/tips-for-everyday-living/"));
            supportList.add(new WellbeingSupportItem("NHS", "Information about NHS mental health services.", R.drawable.help_icon_nhs, "https://www.nhs.uk/using-the-nhs/nhs-services/mental-health-services/"));
            supportList.add(new WellbeingSupportItem("Self-help resources", "A selection of self-help resources.", R.drawable.help_icon_self_help, "https://www.annafreud.org/on-my-mind/self-care/"));
        }

        return supportList;
    }
}
