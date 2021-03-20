package com.joshuarichardson.fivewaystowellbeing;

public class ActivityTypeImageHelper {
    public static int getActivityImage(String type) {
        try {
            // Catch the exception if the user does not set a value
            ActivityType activityType;
            try {
                activityType = ActivityType.valueOf(type.toUpperCase());
            } catch(IllegalArgumentException e) {
                return 0;
            }

            switch (activityType) {
                case APP:
                    return R.drawable.activity_type_app;
                case SPORT:
                    return R.drawable.activity_type_sport;
                case PET:
                    return R.drawable.activity_type_pet;
                case HOBBY:
                    return R.drawable.activity_type_hobby;
                case WORK:
                    return R.drawable.activity_type_work;
                case LEARNING:
                    return R.drawable.activity_type_learning;
                case CHORES:
                    return R.drawable.activity_type_chores;
                case COOKING:
                    return R.drawable.activity_type_cooking;
                case EXERCISE:
                    return R.drawable.activity_type_exercise;
                case RELAXATION:
                    return R.drawable.activity_type_relaxation;
                case PEOPLE:
                    return R.drawable.activity_type_people;
                case JOURNALING:
                    return R.drawable.activity_type_journaling;
                case FAITH:
                    return R.drawable.activity_type_faith;
                default:
                    return 0;
            }
        } catch (Exception e){
            return 0;
        }
    }
}
