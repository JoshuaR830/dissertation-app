package com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.AutomaticActivityTypes;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER;
import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT;
import static com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking.PhysicalActivityDurationIntentService.END_ACTIVITY;
import static com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking.PhysicalActivityDurationIntentService.START_ACTIVITY;

/**
 * Broadcast receiver runs when an activity transition occurs
 * Start the ActivityDurationIntentService to ensure that activities are processed correctly
 */
@AndroidEntryPoint
public class PhysicalActivityBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Reference https://developers.google.com/android/reference/com/google/android/gms/location/ActivityRecognitionClient#requestActivityTransitionUpdates(com.google.android.gms.location.ActivityTransitionRequest,%20android.app.PendingIntent)
        if(!ActivityTransitionResult.hasResult(intent)) {
            return;
        }

        ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);

        if(result == null) {
            return;
        }

        for (ActivityTransitionEvent event : result.getTransitionEvents()){
            Intent serviceIntent = new Intent(context, PhysicalActivityDurationIntentService.class);
            Bundle bundle = new Bundle();

            bundle.putLong("elapsed_time", event.getElapsedRealTimeNanos());
            bundle.putString("event_type", getEventTypeString(event.getActivityType()));

            serviceIntent.putExtras(bundle);
            serviceIntent.setAction(getTransitionTypeString(event.getTransitionType()));

            context.startService(serviceIntent);
        }
    }

    /**
     * Get the event type as a string
     *
     * @param detectedActivity The activity type detected
     * @return A string representing the activity type
     */
    private String getEventTypeString(int detectedActivity) {
        switch(detectedActivity) {
            case DetectedActivity.WALKING:
                return AutomaticActivityTypes.WALK;
            case DetectedActivity.RUNNING:
                return AutomaticActivityTypes.RUN;
            case DetectedActivity.ON_BICYCLE:
                return AutomaticActivityTypes.CYCLE;
            case DetectedActivity.IN_VEHICLE:
                return AutomaticActivityTypes.VEHICLE;
            default:
                return null;
        }
    }

    /**
     * Get a string representing the type of transition that occurred
     *
     * @param transitionType The transition type
     * @return A string representing the activity type
     */
    private String getTransitionTypeString(int transitionType) {
        if(transitionType == ACTIVITY_TRANSITION_ENTER) {
            return START_ACTIVITY;
        }

        if(transitionType == ACTIVITY_TRANSITION_EXIT) {
            return END_ACTIVITY;
        }

        return null;
    }
}
