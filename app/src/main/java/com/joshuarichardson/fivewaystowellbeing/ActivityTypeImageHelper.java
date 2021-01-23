package com.joshuarichardson.fivewaystowellbeing;

public class ActivityTypeImageHelper {
    public static int getActivityImage(String type) {
        try {
            ActivityType activityType = ActivityType.valueOf(type.toUpperCase());

            switch (activityType) {
                case APP:
                    return R.drawable.activity_type_app;
                case SPORT:
                    return R.drawable.activity_type_sport;
                default:
                    return 0;
            }
        } catch (Exception e){
            return 0;
        }
    }
}
