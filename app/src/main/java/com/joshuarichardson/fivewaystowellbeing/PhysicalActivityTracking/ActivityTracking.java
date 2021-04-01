package com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;
import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.Arrays;

import androidx.core.app.NotificationCompat;

public class ActivityTracking {

    public static final int PHYSICAL_ACTIVITY_NOTIFICATION = 5;

    public void initialiseTracking(Context context) {
        ActivityTransitionRequest request = new ActivityTransitionRequest(Arrays.asList(
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
        ));

        Intent intent = new Intent(context, ActivityReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);

        Task<Void> task = ActivityRecognition.getClient(context)
            .requestActivityTransitionUpdates(request, pending);

        task.addOnSuccessListener((result) -> {});
        task.addOnFailureListener((exception) -> {});
    }

    public void sendActivityNotification(Context context) {
        Intent intent = new Intent(context, AddPhysicalActivityIntentService.class);

        // ToDo - send a specific notification when activity selected
        Bundle bundle = new Bundle();
        bundle.putLong("activity_id", 1);
        bundle.putLong("start_time", 1617192504000L);
        bundle.putLong("end_time", 1617203504000L);
        intent.putExtras(bundle);

        PendingIntent confirmPendingIntent = PendingIntent.getService(context, 0, intent, 0);

        // What notification should do on cancel press
        Intent cancelIntent = new Intent(context, AddPhysicalActivityIntentService.class);
        Bundle cancelBundle = new Bundle();
        cancelBundle.putLong("activity_id", -1);
        cancelIntent.putExtras(cancelBundle);

        PendingIntent cancelPendingIntent = PendingIntent.getService(context, 1, cancelIntent, 0);

        NotificationManager notification = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel("CHANNEL_ID_AUTO_ACTIVITY", context.getString(R.string.activity_channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID_AUTO_ACTIVITY")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        // ToDo: use string resources
        builder
            .setSmallIcon(R.drawable.notification_icon_walk)
            .addAction(R.drawable.ic_star_logo, "Yes", confirmPendingIntent)
            .addAction(R.drawable.icon_delete, "No", cancelPendingIntent)
            .setContentTitle("Did you complete a walk");

        notification.notify(PHYSICAL_ACTIVITY_NOTIFICATION, builder.build());
    }
}