package com.joshuarichardson.fivewaystowellbeing.physical_activity_tracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

public class ActivityTracking {

    public static final int PHYSICAL_ACTIVITY_NOTIFICATION_WALK = 5;
    public static final int PHYSICAL_ACTIVITY_NOTIFICATION_RUN = 6;
    public static final int PHYSICAL_ACTIVITY_NOTIFICATION_CYCLE = 7;
    public static final int PHYSICAL_ACTIVITY_NOTIFICATION_VEHICLE = 8;
    private static final String CHANNEL_ID_AUTO_ACTIVITY = "CHANNEL_ID_AUTO_ACTIVITY";

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
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Task<Void> task = ActivityRecognition.getClient(context)
            .requestActivityTransitionUpdates(request, pending);

        task.addOnSuccessListener((result) -> {});
        task.addOnFailureListener((exception) -> {});
    }

    public void sendActivityNotification(Context context, long activityId, long startTime, long endTime, String physicalActivityType) {
        Intent intent = new Intent(context, AddPhysicalActivityIntentService.class);

        Bundle bundle = new Bundle();
        bundle.putLong("activity_id", activityId);
        bundle.putLong("start_time", startTime);
        bundle.putLong("end_time", endTime);
        bundle.putString("event_type", physicalActivityType);
        intent.putExtras(bundle);

        PendingIntent confirmPendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // What notification should do on cancel press
        Intent cancelIntent = new Intent(context, AddPhysicalActivityIntentService.class);
        Bundle cancelBundle = new Bundle();
        cancelBundle.putLong("activity_id", -1);
        cancelIntent.putExtras(cancelBundle);

        PendingIntent cancelPendingIntent = PendingIntent.getService(context, 2, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notification = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel(CHANNEL_ID_AUTO_ACTIVITY, context.getString(R.string.activity_channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_AUTO_ACTIVITY)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        builder
            .addAction(R.drawable.ic_star_logo, context.getString(R.string.button_yes), confirmPendingIntent)
            .addAction(R.drawable.icon_delete, context.getString(R.string.button_no), cancelPendingIntent)
            .setColor(context.getColor(R.color.colorPrimary));

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

        switch(physicalActivityType) {
            case PhysicalActivityTypes.WALK:
                builder
                    .setSmallIcon(R.drawable.notification_icon_walk)
                    .setContentTitle(context.getString(R.string.is_walk_complete))
                    .setContentText(preference.getString("notification_auto_tracking_list_walk", ""));
                notification.notify(PHYSICAL_ACTIVITY_NOTIFICATION_WALK, builder.build());
                break;
            case PhysicalActivityTypes.RUN:
                builder
                    .setSmallIcon(R.drawable.notification_icon_run)
                    .setContentTitle(context.getString(R.string.is_run_complete))
                    .setContentText(preference.getString("notification_auto_tracking_list_run", ""));
                notification.notify(PHYSICAL_ACTIVITY_NOTIFICATION_RUN, builder.build());
                break;
            case PhysicalActivityTypes.CYCLE:
                builder
                    .setSmallIcon(R.drawable.notification_icon_bike)
                    .setContentTitle(context.getString(R.string.is_cycle_complete))
                    .setContentText(preference.getString("notification_auto_tracking_list_cycle", ""));
                notification.notify(PHYSICAL_ACTIVITY_NOTIFICATION_CYCLE, builder.build());
                break;
            case PhysicalActivityTypes.VEHICLE:
                builder
                    .setSmallIcon(R.drawable.notification_icon_vehicle)
                    .setContentTitle(context.getString(R.string.is_drive_complete))
                    .setContentText(preference.getString("notification_auto_tracking_list_vehicle", ""));
                notification.notify(PHYSICAL_ACTIVITY_NOTIFICATION_VEHICLE, builder.build());
                break;
            default:
                break;
        }
    }
}