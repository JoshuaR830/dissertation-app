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
import com.joshuarichardson.fivewaystowellbeing.NotificationConfiguration;
import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.core.app.NotificationCompat;

public class SendCompleteSurveyNotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notification = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel(NotificationConfiguration.ChannelsId.CHANNEL_ID_SURVEYS, context.getResources().getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        if (intent.getExtras() == null || !intent.hasExtra("time")) {
            return;
        }

        // Schedule the next alarm
        AlarmHelper helper =  AlarmHelper.getInstance();

        // Create the pending intent to go to today's wellbeing log
        Intent dailyIntent = new Intent(context, MainActivity.class);
        dailyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dailyPendingIntent = PendingIntent.getActivity(context, NotificationConfiguration.RequestIds.REQUEST_CODE_MAIN_ACTIVITY, dailyIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationConfiguration.ChannelsId.CHANNEL_ID_SURVEYS)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(dailyPendingIntent);

        switch(intent.getExtras().getString("time", "")) {
            // Create a notification when alarm triggers
            case "morning":
                builder
                    .setSmallIcon(R.drawable.icon_notification_morning)
                    .setContentTitle(context.getResources().getString(R.string.notification_morning));
                notification.notify(NotificationConfiguration.NotificationsId.SURVEY_REMINDER, builder.build());
                helper.scheduleNotification(context, "morning");
                break;
            case "noon":
                builder
                    .setSmallIcon(R.drawable.icon_notification_noon)
                    .setContentTitle(context.getResources().getString(R.string.notification_noon));
                notification.notify(NotificationConfiguration.NotificationsId.SURVEY_REMINDER, builder.build());
                helper.scheduleNotification(context, "noon");
                break;
            case "night":
                builder
                    .setSmallIcon(R.drawable.icon_notification_night)
                    .setContentTitle(context.getResources().getString(R.string.notification_night));
                notification.notify(NotificationConfiguration.NotificationsId.SURVEY_REMINDER, builder.build());
                helper.scheduleNotification(context, "night");
                break;
        }
    }
}
