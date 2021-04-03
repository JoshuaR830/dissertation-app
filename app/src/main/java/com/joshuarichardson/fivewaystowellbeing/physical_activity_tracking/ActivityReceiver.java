package com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER;
import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT;
import static com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.ActivityDurationIntentService.END_ACTIVITY;
import static com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking.ActivityDurationIntentService.START_ACTIVITY;

@AndroidEntryPoint
public class ActivityReceiver extends BroadcastReceiver {

    // 10 minutes
    public static final long DURATION_THRESHOLD = 600000;

    @Inject
    WellbeingDatabase db;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Reference https://developers.google.com/android/reference/com/google/android/gms/location/ActivityRecognitionClient#requestActivityTransitionUpdates(com.google.android.gms.location.ActivityTransitionRequest,%20android.app.PendingIntent)
        if(!ActivityTransitionResult.hasResult(intent)) {
            return;
        }

        ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
        for (ActivityTransitionEvent event : result.getTransitionEvents()){
            Intent serviceIntent = new Intent(context, ActivityDurationIntentService.class);
            Bundle bundle = new Bundle();
            bundle.putLong("elapsed_time", event.getElapsedRealTimeNanos());

            if(event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                bundle.putString("event_type", PhysicalActivityTypes.WALK);
                serviceIntent.setAction(START_ACTIVITY);
            } else if(event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                bundle.putString("event_type", PhysicalActivityTypes.WALK);
                serviceIntent.setAction(END_ACTIVITY);
            } else if(event.getActivityType() == DetectedActivity.RUNNING && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                bundle.putString("event_type", PhysicalActivityTypes.RUN);
                serviceIntent.setAction(START_ACTIVITY);
            } else if(event.getActivityType() == DetectedActivity.RUNNING && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                bundle.putString("event_type", PhysicalActivityTypes.RUN);
                serviceIntent.setAction(END_ACTIVITY);
            } else if(event.getActivityType() == DetectedActivity.ON_BICYCLE && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                bundle.putString("event_type", PhysicalActivityTypes.CYCLE);
                serviceIntent.setAction(START_ACTIVITY);
            } else if(event.getActivityType() == DetectedActivity.ON_BICYCLE && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                bundle.putString("event_type", PhysicalActivityTypes.CYCLE);
                serviceIntent.setAction(END_ACTIVITY);
            } else if(event.getActivityType() == DetectedActivity.IN_VEHICLE && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                bundle.putString("event_type", PhysicalActivityTypes.VEHICLE);
                serviceIntent.setAction(START_ACTIVITY);
            } else if(event.getActivityType() == DetectedActivity.IN_VEHICLE && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                bundle.putString("event_type", PhysicalActivityTypes.VEHICLE);
                serviceIntent.setAction(END_ACTIVITY);
            }

            serviceIntent.putExtras(bundle);
            context.startService(serviceIntent);
        }
    }
}