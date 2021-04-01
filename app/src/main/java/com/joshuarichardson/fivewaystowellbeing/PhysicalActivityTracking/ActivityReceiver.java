package com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.core.app.NotificationCompat;

import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER;
import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT;

public class ActivityReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID_AUTO_ACTIVITY = "Automatic activity";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Reference https://developers.google.com/android/reference/com/google/android/gms/location/ActivityRecognitionClient#requestActivityTransitionUpdates(com.google.android.gms.location.ActivityTransitionRequest,%20android.app.PendingIntent)
        if(ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()){
                if(event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                    sendNotification(context, R.drawable.notification_icon_vehicle, R.string.start_walking, 13);
                } else if(event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                    // ToDo - need to get the start time from the database
                    // ToDo - log the end time in the database - maybe not necessary
                    // ToDo - if active for longer than 10 minutes - then send a notification that allows the user to log the activity
                    sendNotification(context, R.drawable.notification_icon_vehicle, R.string.stop_walking, 23);
                } else if(event.getActivityType() == DetectedActivity.RUNNING && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                    sendNotification(context, R.drawable.notification_icon_vehicle, R.string.start_running, 14);
                } else if(event.getActivityType() == DetectedActivity.RUNNING && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                    sendNotification(context, R.drawable.notification_icon_vehicle, R.string.stop_running, 24);
                } else if(event.getActivityType() == DetectedActivity.ON_BICYCLE && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                    sendNotification(context, R.drawable.notification_icon_vehicle, R.string.start_cycling, 15);
                } else if(event.getActivityType() == DetectedActivity.ON_BICYCLE && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                    sendNotification(context, R.drawable.notification_icon_vehicle, R.string.stop_cycling, 25);
                } else if(event.getActivityType() == DetectedActivity.IN_VEHICLE && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                    sendNotification(context, R.drawable.notification_icon_vehicle, R.string.enter_vehicle, 16);
                } else if(event.getActivityType() == DetectedActivity.IN_VEHICLE && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                    sendNotification(context, R.drawable.notification_icon_vehicle, R.string.exit_vehicle, 26);
                }
            }
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