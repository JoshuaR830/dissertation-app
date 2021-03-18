package com.joshuarichardson.fivewaystowellbeing.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.core.app.NotificationCompat;

public class SendCompleteSurveyNotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID_SURVEYS = "survey_notification_channel";
    private static final int MORNING_REMINDER = 1;
    private static final int NOON_REMINDER = 2;
    private static final int NIGHT_REMINDER = 3;

    // This will replace the notification instead of sending a new one
    public static final int SURVEY_REMINDER = 4;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notification = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel(CHANNEL_ID_SURVEYS, context.getResources().getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        if (intent.getExtras() == null || !intent.hasExtra("time")) {
            return;
        }

        // Schedule the next alarm
        AlarmHelper helper =  AlarmHelper.getInstance();

        // Create the pending intent to go to today's wellbeing log
        Intent dailyIntent = new Intent(context, MainActivity.class);
        dailyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dailyPendingIntent = PendingIntent.getActivity(context, 0, dailyIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_SURVEYS)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(dailyPendingIntent);

        switch(intent.getExtras().getString("time", "")) {
            // Create a notification when alarm triggers
            case "morning":
                builder
                    .setSmallIcon(R.drawable.icon_notification_morning)
                    .setContentTitle(context.getResources().getString(R.string.notification_morning));
                notification.notify(SURVEY_REMINDER, builder.build());
                helper.scheduleNotification(context, "morning");
                break;
            case "noon":
                builder
                    .setSmallIcon(R.drawable.icon_notification_noon)
                    .setContentTitle(context.getResources().getString(R.string.notification_noon));
                notification.notify(SURVEY_REMINDER, builder.build());
                helper.scheduleNotification(context, "noon");
                break;
            case "night":
                builder
                    .setSmallIcon(R.drawable.icon_notification_night)
                    .setContentTitle(context.getResources().getString(R.string.notification_night));
                notification.notify(SURVEY_REMINDER, builder.build());
                helper.scheduleNotification(context, "night");
                break;
        }
    }
}
