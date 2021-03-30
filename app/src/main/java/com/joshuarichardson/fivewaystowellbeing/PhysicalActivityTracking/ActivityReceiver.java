package com.joshuarichardson.fivewaystowellbeing.PhysicalActivityTracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.core.app.NotificationCompat;

import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER;
import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT;

public class ActivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Started", "Receiver received something");


        NotificationManager notification = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel("CHANNEL_ID_SURVEYS", "Name", NotificationManager.IMPORTANCE_DEFAULT));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID_SURVEYS")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);


        builder
            .setSmallIcon(R.drawable.icon_notification_morning)
            .setContentTitle("Receiver received something");

        notification.notify(3, builder.build());

        Log.d("Notification sent", "Notified");

        // Reference https://developers.google.com/android/reference/com/google/android/gms/location/ActivityRecognitionClient#requestActivityTransitionUpdates(com.google.android.gms.location.ActivityTransitionRequest,%20android.app.PendingIntent)
        if(ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);

//            builder.setContentTitle("has result");
//            notification.notify(4, builder.build());

            for (ActivityTransitionEvent event : result.getTransitionEvents()){
                if(event.getActivityType() == DetectedActivity.STILL && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                    builder.setContentTitle("started still");
                    notification.notify(12, builder.build());
                }

                if(event.getActivityType() == DetectedActivity.STILL && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                    builder.setContentTitle("stopped still");
                    notification.notify(22, builder.build());
                }

                if(event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ACTIVITY_TRANSITION_ENTER) {
                    builder.setContentTitle("started moving");
                    notification.notify(13, builder.build());
                }

                if(event.getActivityType() == DetectedActivity.WALKING && event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                    builder.setContentTitle("stopped moving");
                    notification.notify(23, builder.build());
                }
            }
        }
    }
}