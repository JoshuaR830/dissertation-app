package com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.physical_activity_tracking;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;
import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.NotificationConfiguration;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking.AddAutomaticActivityIntentService;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class ActivityTracking {

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

        // Handle different permissions on different device SDKs
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check that permissions have been granted for devices that require it
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                startTracking(context, request, pending);
            }
        } else {
            startTracking(context, request, pending);
        }
    }

    private void startTracking(Context context, ActivityTransitionRequest request, PendingIntent pending) {
        Task<Void> task = ActivityRecognition.getClient(context)
            .requestActivityTransitionUpdates(request, pending);

        task.addOnSuccessListener((result) -> {});
        task.addOnFailureListener((exception) -> {
            exception.printStackTrace();
        });
    }

    public void sendActivityNotification(Context context, long activityId, long startTime, long endTime, String physicalActivityType, @Nullable String appName, @Nullable String activityName) {
        if (activityId <= 0) {
            return;
        }
        Intent intent = new Intent(context, AddAutomaticActivityIntentService.class);

        Bundle bundle = new Bundle();
        bundle.putLong("activity_id", activityId);
        bundle.putLong("start_time", startTime);
        bundle.putLong("end_time", endTime);
        bundle.putString("event_type", physicalActivityType);

        int acceptRequestCode;
        int cancelRequestCode;

        // Unique codes for each type of event means that the notifications should behave as expected
        switch(physicalActivityType) {
            case AutomaticActivityTypes.WALK:
                acceptRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_WALK_ACCEPT;
                cancelRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_WALK_REJECT;
                bundle.putString("app_name", context.getString(R.string.walk));
                break;
            case AutomaticActivityTypes.RUN:
                acceptRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_RUN_ACCEPT;
                cancelRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_RUN_REJECT;
                bundle.putString("app_name", context.getString(R.string.run));
                break;
            case AutomaticActivityTypes.CYCLE:
                acceptRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_CYCLE_ACCEPT;
                cancelRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_CYCLE_REJECT;
                bundle.putString("app_name", context.getString(R.string.cycle));
                break;
            case AutomaticActivityTypes.VEHICLE:
                acceptRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_VEHICLE_ACCEPT;
                cancelRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_VEHICLE_REJECT;
                bundle.putString("app_name", context.getString(R.string.vehicle));
                break;
            default:
                acceptRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_APP_ACCEPT;
                cancelRequestCode = NotificationConfiguration.RequestIds.REQUEST_CODE_APP_REJECT;
                bundle.putString("app_name", appName);
                break;
        }

        intent.putExtras(bundle);

        PendingIntent confirmPendingIntent = PendingIntent.getService(context, acceptRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // What notification should do on cancel press
        Intent cancelIntent = new Intent(context, AddAutomaticActivityIntentService.class);
        Bundle cancelBundle = new Bundle();
        cancelBundle.putLong("activity_id", -1);
        cancelBundle.putString("event_type", physicalActivityType);
        cancelIntent.putExtras(cancelBundle);

        PendingIntent cancelPendingIntent = PendingIntent.getService(context, cancelRequestCode, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notification = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel(NotificationConfiguration.ChannelsId.CHANNEL_ID_AUTO_ACTIVITY, context.getString(R.string.activity_channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        // Create the pending intent to go to today's wellbeing log
        Intent dailyIntent = new Intent(context, MainActivity.class);
        dailyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dailyPendingIntent = PendingIntent.getActivity(context, NotificationConfiguration.RequestIds.REQUEST_CODE_MAIN_ACTIVITY, dailyIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationConfiguration.ChannelsId.CHANNEL_ID_AUTO_ACTIVITY)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(dailyPendingIntent)
            .addAction(R.drawable.ic_star_logo, context.getString(R.string.button_yes), confirmPendingIntent)
            .addAction(R.drawable.icon_delete, context.getString(R.string.button_no), cancelPendingIntent)
            .setColor(context.getColor(R.color.colorPrimary));

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

        switch(physicalActivityType) {
            case AutomaticActivityTypes.WALK:
                builder
                    .setSmallIcon(R.drawable.notification_icon_walk)
                    .setContentTitle(context.getString(R.string.is_walk_complete))
                    .setContentText(String.format("%s %s", context.getString(R.string.logging_message), preference.getString("notification_auto_tracking_list_walk", "")));
                notification.notify(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_WALK, builder.build());
                break;
            case AutomaticActivityTypes.RUN:
                builder
                    .setSmallIcon(R.drawable.notification_icon_run)
                    .setContentTitle(context.getString(R.string.is_run_complete))
                    .setContentText(String.format("%s %s", context.getString(R.string.logging_message), preference.getString("notification_auto_tracking_list_run", "")));
                notification.notify(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_RUN, builder.build());
                break;
            case AutomaticActivityTypes.CYCLE:
                builder
                    .setSmallIcon(R.drawable.notification_icon_bike)
                    .setContentTitle(context.getString(R.string.is_cycle_complete))
                    .setContentText(String.format("%s %s", context.getString(R.string.logging_message), preference.getString("notification_auto_tracking_list_cycle", "")));
                notification.notify(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_CYCLE, builder.build());
                break;
            case AutomaticActivityTypes.VEHICLE:
                builder
                    .setSmallIcon(R.drawable.notification_icon_vehicle)
                    .setContentTitle(context.getString(R.string.is_drive_complete))
                    .setContentText(String.format("%s %s", context.getString(R.string.logging_message), preference.getString("notification_auto_tracking_list_vehicle", "")));
                notification.notify(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_VEHICLE, builder.build());
                break;
            default:
                if(appName == null) {
                    appName = context.getString(R.string.app_placeholder);
                }

                if(activityName == null) {
                    activityName = context.getString(R.string.activity_placeholder);
                }

                builder
                    .setSmallIcon(R.drawable.notification_icon_phone)
                    .setContentTitle(String.format("%s %s %s", context.getString(R.string.is_app_add), appName, context.getString(R.string.is_app_complete)))
                    .setContentText(String.format("%s %s", context.getString(R.string.logging_message), activityName));
                notification.notify(NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_APP, builder.build());
                break;
        }
    }
}