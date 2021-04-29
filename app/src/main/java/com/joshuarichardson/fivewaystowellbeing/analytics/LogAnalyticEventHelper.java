package com.joshuarichardson.fivewaystowellbeing.analytics;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import javax.inject.Inject;

/**
 * Class containing helper functions to log analytics events
 */
public class LogAnalyticEventHelper {

    private final FirebaseAnalytics firebaseAnalytics;

    // Injecting this into an activity will allow it to be used to track events
    // Injecting the firebase module allows access to log things in Firebase
    @Inject
    public LogAnalyticEventHelper(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    /**
     * Log activity creation event to Firebase
     * @param currentActivity The calling class
     */
    public void logCreateActivityEvent(Object currentActivity) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivity.getClass().getSimpleName());
        this.firebaseAnalytics.logEvent(AnalyticEvents.Events.CREATE_ACTIVITY, analyticsBundle);
    }

    /**
     * Log survey creation events in Firebase
     * @param currentActivity The calling class
     */
    public void logCreateSurveyEvent(Object currentActivity) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivity.getClass().getSimpleName());
        analyticsBundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticEvents.Param.SURVEY);
        this.firebaseAnalytics.logEvent(AnalyticEvents.Events.CREATE_SURVEY, analyticsBundle);
    }

    /**
     * Log ways to wellbeing events in firebase
     *
     * @param currentActivity The calling class
     * @param waysToWellbeing The way to wellbeing associated with the event
     */
    public void logWayToWellbeingEvent(Object currentActivity, WaysToWellbeing waysToWellbeing) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivity.getClass().getSimpleName());
        analyticsBundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticEvents.Param.WAY_TO_WELLBEING);

        // Log a specific way to wellbeing event
        this.firebaseAnalytics.logEvent(waysToWellbeing.toString().toLowerCase(), analyticsBundle);

        // Log a generic way to wellbeing event
        analyticsBundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, waysToWellbeing.toString().toLowerCase());
        this.firebaseAnalytics.logEvent(AnalyticEvents.Events.ACHIEVED_WAY_TO_WELLBEING, analyticsBundle);
    }

    /**
     * Log when a sub-activity is checked in Firebase
     *
     * @param currentActivity The calling class
     * @param waysToWellbeing The way to wellbeing that the event involves
     * @param isChecked The status of the checkbox for logging checked/unchecked
     */
    public void logWayToWellbeingChecked(Object currentActivity, WaysToWellbeing waysToWellbeing, boolean isChecked) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivity.getClass().getSimpleName());
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

    /**
     * Log when an activity is logged in Firebase
     *
     * @param currentActivity The calling class
     * @param waysToWellbeing The way to wellbeing that the event involves
     */
    public void logWayToWellbeingActivity(Object currentActivity, WaysToWellbeing waysToWellbeing) {
        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, currentActivity.getClass().getSimpleName());
        analyticsBundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticEvents.Param.WAY_TO_WELLBEING);

        // Log a specific way to wellbeing event
        this.firebaseAnalytics.logEvent(waysToWellbeing.toString().toLowerCase(), analyticsBundle);

        // Log a generic way to wellbeing event
        analyticsBundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, waysToWellbeing.toString().toLowerCase());
        this.firebaseAnalytics.logEvent(AnalyticEvents.Events.ACTIVITY_WAY_TO_WELLBEING, analyticsBundle);
    }

    /**
     * Log automatic activity logging events in Firebase
     *
     * @param currentActivityObject The calling class
     * @param waysToWellbeing The way to wellbeing that the event involves
     */
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
