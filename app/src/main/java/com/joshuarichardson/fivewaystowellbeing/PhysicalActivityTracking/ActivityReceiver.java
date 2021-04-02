package com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;

import javax.inject.Inject;

import androidx.core.app.NotificationCompat;
import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER;
import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT;
import static com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking.ActivityDurationIntentService.END_ACTIVITY;
import static com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking.ActivityDurationIntentService.START_ACTIVITY;

@AndroidEntryPoint
public class ActivityReceiver extends BroadcastReceiver {

    // 10 minutes
    public static final long DURATION_THRESHOLD = 600000;

    @Inject
    WellbeingDatabase db;

    public static final String CHANNEL_ID_AUTO_ACTIVITY = "Automatic activity";

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
                sendNotification(context, R.drawable.notification_icon_walk, R.string.start_walking, 13);
            } else if(event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                bundle.putString("event_type", PhysicalActivityTypes.WALK);
                serviceIntent.setAction(END_ACTIVITY);
                sendNotification(context, R.drawable.notification_icon_walk, R.string.stop_walking, 23);
            } else if(event.getActivityType() == DetectedActivity.RUNNING && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                bundle.putString("event_type", PhysicalActivityTypes.RUN);
                serviceIntent.setAction(START_ACTIVITY);
                sendNotification(context, R.drawable.notification_icon_run, R.string.start_running, 14);
            } else if(event.getActivityType() == DetectedActivity.RUNNING && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                bundle.putString("event_type", PhysicalActivityTypes.RUN);
                serviceIntent.setAction(END_ACTIVITY);
                sendNotification(context, R.drawable.notification_icon_run, R.string.stop_running, 24);
            } else if(event.getActivityType() == DetectedActivity.ON_BICYCLE && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                bundle.putString("event_type", PhysicalActivityTypes.CYCLE);
                serviceIntent.setAction(START_ACTIVITY);
                sendNotification(context, R.drawable.notification_icon_bike, R.string.start_cycling, 15);
            } else if(event.getActivityType() == DetectedActivity.ON_BICYCLE && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                bundle.putString("event_type", PhysicalActivityTypes.CYCLE);
                serviceIntent.setAction(END_ACTIVITY);
                sendNotification(context, R.drawable.notification_icon_bike, R.string.stop_cycling, 25);
            } else if(event.getActivityType() == DetectedActivity.IN_VEHICLE && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                bundle.putString("event_type", PhysicalActivityTypes.VEHICLE);
                serviceIntent.setAction(START_ACTIVITY);
                sendNotification(context, R.drawable.notification_icon_vehicle, R.string.enter_vehicle, 16);
            } else if(event.getActivityType() == DetectedActivity.IN_VEHICLE && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                bundle.putString("event_type", PhysicalActivityTypes.VEHICLE);
                serviceIntent.setAction(END_ACTIVITY);
                sendNotification(context, R.drawable.notification_icon_vehicle, R.string.exit_vehicle, 26);
            }

            serviceIntent.putExtras(bundle);
            context.startService(serviceIntent);
        }
    }

    public void sendNotification(Context context, int iconResource, int textResource, int notificationId) {
        NotificationManager notification = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel(CHANNEL_ID_AUTO_ACTIVITY, context.getString(R.string.activity_channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_AUTO_ACTIVITY)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        builder
            .setSmallIcon(iconResource)
            .setContentTitle(context.getString(textResource));

        notification.notify(notificationId, builder.build());
    }
}