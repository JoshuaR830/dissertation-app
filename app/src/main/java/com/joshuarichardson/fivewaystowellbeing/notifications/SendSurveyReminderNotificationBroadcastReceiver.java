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
import com.joshuarichardson.fivewaystowellbeing.NotificationInfo;
import com.joshuarichardson.fivewaystowellbeing.R;

import javax.inject.Inject;

import androidx.core.app.NotificationCompat;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A broadcast receiver which when triggered sends a notification to a user
 * reminding them to log their activities
 */
@AndroidEntryPoint
public class SendSurveyReminderNotificationBroadcastReceiver extends BroadcastReceiver {

    @Inject
    AlarmHelper alarmHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notification = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel(NotificationConfiguration.ChannelsId.CHANNEL_ID_SURVEYS, context.getResources().getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        if (intent.getExtras() == null || !intent.hasExtra("time")) {
            return;
        }

        // Create the pending intent to go to today's wellbeing log
        Intent dailyIntent = new Intent(context, MainActivity.class);
        dailyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dailyPendingIntent = PendingIntent.getActivity(context, NotificationConfiguration.RequestIds.REQUEST_CODE_MAIN_ACTIVITY, dailyIntent, 0);

        NotificationInfo info = getNotificationInfo(context, intent);

        if(info == null) {
            return;
        }

        // Create a notification when alarm triggers
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationConfiguration.ChannelsId.CHANNEL_ID_SURVEYS)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(dailyPendingIntent)
            .setSmallIcon(info.getIconId())
            .setContentTitle(info.getTitle());

        notification.notify(info.getNotificationId(), builder.build());
        alarmHelper.scheduleNotification(context, intent.getExtras().getString("time", ""));

    }

    /**
     * Get the notification info required for specified time of day
     *
     * @param context The application context
     * @param intent The intent passed to the broadcast receiver
     * @return An object containing the information required for a new notification
     */
    private NotificationInfo getNotificationInfo(Context context, Intent intent) {
        switch(intent.getExtras().getString("time", "")) {
            case "morning":
                return new NotificationInfo(
                    R.drawable.icon_notification_morning,
                    context.getResources().getString(R.string.notification_morning),
                    NotificationConfiguration.NotificationsId.SURVEY_REMINDER
                );
            case "noon":
                return new NotificationInfo(
                    R.drawable.icon_notification_noon,
                    context.getResources().getString(R.string.notification_noon),
                    NotificationConfiguration.NotificationsId.SURVEY_REMINDER
                );
            case "night":
                return new NotificationInfo(
                    R.drawable.icon_notification_night,
                    context.getResources().getString(R.string.notification_night),
                    NotificationConfiguration.NotificationsId.SURVEY_REMINDER
                );
            default:
                return null;
        }
    }
}
