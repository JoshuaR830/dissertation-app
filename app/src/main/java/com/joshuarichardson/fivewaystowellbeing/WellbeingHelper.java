package com.joshuarichardson.fivewaystowellbeing;

public class WellbeingHelper {
    public static int getImage(WaysToWellbeing surveyResponseWayToWellbeing) {
        switch(surveyResponseWayToWellbeing) {
            case CONNECT:
                return R.drawable.icon_way_to_wellbeing_connect;
            case KEEP_LEARNING:
                return R.drawable.icon_way_to_wellbeing_keep_learning;
            case TAKE_NOTICE:
                return R.drawable.icon_way_to_wellbeing_take_notice;
            case BE_ACTIVE:
                return R.drawable.icon_way_to_wellbeing_be_active;
            case GIVE:
                return R.drawable.icon_way_to_wellbeing_give;
            default:
                return 0;
        }
    }
}
