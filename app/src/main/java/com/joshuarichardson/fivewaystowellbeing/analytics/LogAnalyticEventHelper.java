package com.joshuarichardson.fivewaystowellbeing.analytics;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import javax.inject.Inject;

public class LogAnalyticEventHelper {

    private final FirebaseAnalytics firebaseAnalytics;

    // Injecting this into an activity will allow it to be used to track stuff
    // Injecting the firebase module allows access to log things in Firebase
    @Inject
    public LogAnalyticEventHelper(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    // Call to log a specific event
    public void logCreatePasstimeEvent(Object currentActivityObject) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivityObject.getClass().getSimpleName());
        this.firebaseAnalytics.logEvent(AnalyticEvents.Events.CREATE_PASSTIME, analyticsBundle);
    }

    public void logCreateSurveyEvent(Object currentActivityObject) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivityObject.getClass().getSimpleName());
        analyticsBundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticEvents.Param.SURVEY);
        this.firebaseAnalytics.logEvent(AnalyticEvents.Events.CREATE_SURVEY, analyticsBundle);
    }

    public void logWayToWellbeingEvent(Object currentActivityObject, WaysToWellbeing waysToWellbeing) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivityObject.getClass().getSimpleName());
        analyticsBundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticEvents.Param.WAY_TO_WELLBEING);

        // Log a specific way to wellbeing event
        this.firebaseAnalytics.logEvent(waysToWellbeing.toString().toLowerCase(), analyticsBundle);

        // Log a generic way to wellbeing event
        analyticsBundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, waysToWellbeing.toString().toLowerCase());
        this.firebaseAnalytics.logEvent(AnalyticEvents.Events.ACHIEVED_WAY_TO_WELLBEING, analyticsBundle);
    }

    public void logWayToWellbeingChecked(Object currentActivityObject, WaysToWellbeing waysToWellbeing, boolean isChecked) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivityObject.getClass().getSimpleName());
        analyticsBundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticEvents.Param.WAY_TO_WELLBEING);

        // Log a specific way to wellbeing event
        this.firebaseAnalytics.logEvent(waysToWellbeing.toString().toLowerCase(), analyticsBundle);

        // Log a generic way to wellbeing event
        analyticsBundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, waysToWellbeing.toString().toLowerCase());

        if(isChecked)
            this.firebaseAnalytics.logEvent(AnalyticEvents.Events.CHECKED_WAY_TO_WELLBEING, analyticsBundle);
        else {
            this.firebaseAnalytics.logEvent(AnalyticEvents.Events.UNCHECKED_WAY_TO_WELLBEING, analyticsBundle);
        }
    }

    public void logWayToWellbeingActivity(Object currentActivityObject, WaysToWellbeing waysToWellbeing) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivityObject.getClass().getSimpleName());
        analyticsBundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticEvents.Param.WAY_TO_WELLBEING);

        // Log a specific way to wellbeing event
        this.firebaseAnalytics.logEvent(waysToWellbeing.toString().toLowerCase(), analyticsBundle);

        // Log a generic way to wellbeing event
        analyticsBundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, waysToWellbeing.toString().toLowerCase());
        this.firebaseAnalytics.logEvent(AnalyticEvents.Events.ACTIVITY_WAY_TO_WELLBEING, analyticsBundle);
    }

    public void logWayToWellbeingAutomaticActivity(Object currentActivityObject, WaysToWellbeing waysToWellbeing) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivityObject.getClass().getSimpleName());
        analyticsBundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticEvents.Param.WAY_TO_WELLBEING);

        // Log a specific way to wellbeing event
        this.firebaseAnalytics.logEvent(waysToWellbeing.toString().toLowerCase(), analyticsBundle);

        // Log a generic way to wellbeing event
        analyticsBundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, waysToWellbeing.toString().toLowerCase());
        this.firebaseAnalytics.logEvent(AnalyticEvents.Events.AUTOMATIC_WAY_TO_WELLBEING, analyticsBundle);
    }
}
