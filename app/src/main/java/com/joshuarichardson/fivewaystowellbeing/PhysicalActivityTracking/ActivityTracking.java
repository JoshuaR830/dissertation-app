package com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

public class ActivityTracking {

    public void something(Context context) {
//        ActivityRecognitionClient client = new ActivityRecognitionClient(context);
//        client.requestActivityUpdates();
        ActivityTransitionRequest request = new ActivityTransitionRequest(Arrays.asList(
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        ));

//        Task task = ActivityRecognition.getClient(context)
//            .removeActivityTransitionUpdates(request, pending);

        Intent intent = new Intent(context, ActivityReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);

//        Task<Void> task = ActivityRecognition.getClient(context)
//            .requestActivityUpdates(100, pending);

        Task<Void> task = ActivityRecognition.getClient(context)
            .requestActivityTransitionUpdates(request, pending);

        task.addOnSuccessListener((result) -> {
            Log.d("Success", "Success");
            context.startService(intent);
        });

        task.addOnFailureListener((exception) -> {
            Log.d("Failed", exception.getMessage());
        });
//        client.requestActivityTransitionUpdates(request, pending);
    }
}