package com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import androidx.core.content.ContextCompat;

/**
 * A helper class to facilitate tracking
 */
public class PhysicalActivityTracking {

    @Inject
    PhysicalActivityTracking() {}

    /**
     * Set up physical activity tracking
     *
     * @param context The application context
     */
    public void initialiseTracking(Context context) {
        ActivityTransitionRequest activityTransitionRequest = new ActivityTransitionRequest(createTransitionRequest());

        Intent intent = new Intent(context, PhysicalActivityBroadcastReceiver.class);
        PendingIntent pendingBroadcastIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Handle different permissions on different device SDKs
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check that permissions have been granted for devices that require it
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                startTracking(context, activityTransitionRequest, pendingBroadcastIntent);
            }
        } else {
            startTracking(context, activityTransitionRequest, pendingBroadcastIntent);
        }
    }

    /**
     * Begin tracking the activities
     * @param context The application context
     * @param activityTransitionRequest The activity transition request
     * @param pendingBroadcastIntent The intent of the broadcast receiver to trigger
     */
    private void startTracking(Context context, ActivityTransitionRequest activityTransitionRequest, PendingIntent pendingBroadcastIntent) {
        Task<Void> task = ActivityRecognition.getClient(context)
            .requestActivityTransitionUpdates(activityTransitionRequest, pendingBroadcastIntent);

        task.addOnSuccessListener((result) -> {});
        task.addOnFailureListener(Throwable::printStackTrace);
    }

    /**
     * Build a transition request for all transition types that will be used
     *
     * @return A list of all the activity transition types
     */
    private List<ActivityTransition> createTransitionRequest() {
        return Arrays.asList(
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        );
    }
}