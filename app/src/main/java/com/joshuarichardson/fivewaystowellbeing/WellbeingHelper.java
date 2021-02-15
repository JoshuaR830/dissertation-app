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
                return R.drawable.icon_way_to_wellbeing_unassigned;
        }
    }

    public static WaysToWellbeing getDefaultWayToWellbeingFromActivityType(String type) {
        WaysToWellbeing wayToWellbeing;
        switch (ActivityType.valueOf(type.toUpperCase())) {
            case HOBBY:
            case LEARNING:
            case WORK:
                wayToWellbeing = WaysToWellbeing.KEEP_LEARNING;
                break;
            case PET:
                wayToWellbeing = WaysToWellbeing.CONNECT;
                break;
            case SPORT:
            case EXERCISE:
                wayToWellbeing = WaysToWellbeing.BE_ACTIVE;
                break;
            case CHORES:
            case COOKING:
                wayToWellbeing = WaysToWellbeing.GIVE;
                break;
            case RELAXATION:
                wayToWellbeing = WaysToWellbeing.TAKE_NOTICE;
                break;
            case APP:
            default:
                wayToWellbeing = WaysToWellbeing.UNASSIGNED;
                break;
        }

        return wayToWellbeing;
    }

    public static WaysToWellbeing getWayToWellbeingFromString(String wayToWellbeingInput) {

        WaysToWellbeing wayToWellbeing = WaysToWellbeing.UNASSIGNED;
        switch (wayToWellbeingInput.toLowerCase()) {
            case "connect":
                wayToWellbeing = WaysToWellbeing.CONNECT;
                break;
            case "be active":
                wayToWellbeing = WaysToWellbeing.BE_ACTIVE;
                break;
            case "keep learning":
                wayToWellbeing = WaysToWellbeing.KEEP_LEARNING;
                break;
            case "take notice":
                wayToWellbeing = WaysToWellbeing.TAKE_NOTICE;
                break;
            case "give":
                wayToWellbeing = WaysToWellbeing.GIVE;
                break;
            default:
                wayToWellbeing = WaysToWellbeing.UNASSIGNED;
        }

        return wayToWellbeing;
    }

    public static String getStringFromWayToWellbeing(WaysToWellbeing wayToWellbeingInput) {

        String wayToWellbeing = "None";
        switch (wayToWellbeingInput) {
            case CONNECT:
                wayToWellbeing = "Connect";
                break;
            case BE_ACTIVE:
                wayToWellbeing = "Be active";
                break;
            case KEEP_LEARNING:
                wayToWellbeing = "Keep learning";
                break;
            case TAKE_NOTICE:
                wayToWellbeing = "Take notice";
                break;
            case GIVE:
                wayToWellbeing = "Give";
                break;
            default:
                wayToWellbeing = "None";
        }

        return wayToWellbeing;
    }
}
