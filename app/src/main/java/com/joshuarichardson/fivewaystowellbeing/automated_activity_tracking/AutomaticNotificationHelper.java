package com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.NotificationConfiguration;
import com.joshuarichardson.fivewaystowellbeing.R;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Send notifications to users
 * Use for any sort of automated activity tracking
 */
public class AutomaticNotificationHelper {

    private final Context context;

    @Inject
    public AutomaticNotificationHelper(@ApplicationContext Context context) {
        this.context = context;
    }

    /**
     * Send a notification to suggest activities
     *
     * @param activityId The id of the activity which should be logged
     * @param startTime The start time of the activity to log in milliseconds
     * @param endTime The end time of the activity to be logged in milliseconds
     * @param automaticActivityType The type of activity
     * @param appName The name of the app (if it is an app)
     * @param activityName The name of the activity
     */
    public void sendSuggestedActivityNotification(long activityId, long startTime, long endTime, String automaticActivityType, @Nullable String appName, @Nullable String activityName) {
        if (activityId <= 0) {
            return;
        }

        AutomaticActivityStatusInfo automaticActivityStatusInfo = getAutomaticActivityStatusInfo(automaticActivityType, appName);

        // Create the confirmation pending intents
        Intent intent = new Intent(context, AddAutomaticActivityIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putLong("activity_id", activityId);
        bundle.putLong("start_time", startTime);
        bundle.putLong("end_time", endTime);
        bundle.putString("event_type", automaticActivityType);
        bundle.putString("app_name", automaticActivityStatusInfo.getAppName());
        intent.putExtras(bundle);
        PendingIntent confirmPendingIntent = PendingIntent.getService(context, automaticActivityStatusInfo.getAcceptCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create cancellation pending intent
        Intent cancelIntent = new Intent(context, AddAutomaticActivityIntentService.class);
        Bundle cancelBundle = new Bundle();
        cancelBundle.putLong("activity_id", -1);
        cancelBundle.putString("event_type", automaticActivityType);
        cancelIntent.putExtras(cancelBundle);
        PendingIntent cancelPendingIntent = PendingIntent.getService(context, automaticActivityStatusInfo.getRejectCode(), cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the pending intent to go to today's wellbeing log
        Intent dailyIntent = new Intent(context, MainActivity.class);
        dailyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dailyPendingIntent = PendingIntent.getActivity(context, NotificationConfiguration.RequestIds.REQUEST_CODE_MAIN_ACTIVITY, dailyIntent, 0);


        NotificationManager notification = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.createNotificationChannel(new NotificationChannel(NotificationConfiguration.ChannelsId.CHANNEL_ID_AUTO_ACTIVITY, context.getString(R.string.activity_channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AutomaticActivityNotificationInfo notificationInfo = getNotificationInfo(preferences, automaticActivityType, appName, activityName);

        // Create and send the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationConfiguration.ChannelsId.CHANNEL_ID_AUTO_ACTIVITY)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(dailyPendingIntent)
            .addAction(R.drawable.ic_star_logo, context.getString(R.string.button_yes), confirmPendingIntent)
            .addAction(R.drawable.icon_delete, context.getString(R.string.button_no), cancelPendingIntent)
            .setColor(context.getColor(R.color.colorPrimary))
            .setSmallIcon(notificationInfo.getIconId())
            .setContentTitle(notificationInfo.getTitle())
            .setContentText(String.format("%s %s", context.getString(R.string.logging_message), notificationInfo.getActivityName()));

        notification.notify(notificationInfo.getNotificationId(), builder.build());
    }

    /**
     * Retrieve the information required to build a notification for the specified activity type.
     *
     * @param preferences An instance of shared preferences
     * @param automaticActivityType The type of automated activity
     * @param appName The name of the app if it is an app
     * @param activityName The name of the activity
     * @return The details required to build a notification
     */
    private AutomaticActivityNotificationInfo getNotificationInfo(SharedPreferences preferences,  String automaticActivityType, @Nullable String appName, @Nullable String activityName) {
        switch (automaticActivityType) {
            case AutomaticActivityTypes.WALK:
                return new AutomaticActivityNotificationInfo(
                    preferences.getString("notification_auto_tracking_list_walk", ""),
                    context.getString(R.string.is_walk_complete),
                    R.drawable.notification_icon_walk,
                    NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_WALK
                );
            case AutomaticActivityTypes.RUN:
                return new AutomaticActivityNotificationInfo(
                    preferences.getString("notification_auto_tracking_list_run", ""),
                    context.getString(R.string.is_run_complete),
                    R.drawable.notification_icon_run,
                    NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_RUN
                );
            case AutomaticActivityTypes.CYCLE:
                return new AutomaticActivityNotificationInfo(
                    preferences.getString("notification_auto_tracking_list_cycle", ""),
                    context.getString(R.string.is_cycle_complete),
                    R.drawable.notification_icon_bike,
                    NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_CYCLE
                );
            case AutomaticActivityTypes.VEHICLE:
                return new AutomaticActivityNotificationInfo(
                    preferences.getString("notification_auto_tracking_list_vehicle", ""),
                    context.getString(R.string.is_drive_complete),
                    R.drawable.notification_icon_vehicle,
                    NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_VEHICLE
                );
            default:
                if(appName == null) {
                    appName = context.getString(R.string.app_placeholder);
                }

                if(activityName == null) {
                    activityName = context.getString(R.string.activity_placeholder);
                }

                return new AutomaticActivityNotificationInfo(
                    activityName,
                    formatTitleForApp(appName),
                    R.drawable.notification_icon_phone,
                    NotificationConfiguration.NotificationsId.AUTOMATIC_ACTIVITY_NOTIFICATION_APP
                );
        }
    }

    /**
     * Construct a string that forms a title for an applciation
     *
     * @param appName The name of the app that has been used
     * @return The formatted title
     */
    private String formatTitleForApp(String appName) {
        return String.format("%s %s %s", context.getString(R.string.is_app_add), appName, context.getString(R.string.is_app_complete));
    }

    /**
     * Get the status information for the specified activity type
     *
     * @param physicalActivityType The physical activity type
     * @param appName The name of the application that you want to display to users
     * @return The status info of the activity
     */
    private AutomaticActivityStatusInfo getAutomaticActivityStatusInfo(String physicalActivityType, @Nullable String appName) {
        // Unique codes for each type of event means that the notifications should behave as expected
        switch(physicalActivityType) {
            case AutomaticActivityTypes.WALK:
                return new AutomaticActivityStatusInfo(
                    NotificationConfiguration.RequestIds.REQUEST_CODE_WALK_ACCEPT,
                    NotificationConfiguration.RequestIds.REQUEST_CODE_WALK_REJECT,
                    context.getString(R.string.walk)
                );
            case AutomaticActivityTypes.RUN:
                return new AutomaticActivityStatusInfo(
                    NotificationConfiguration.RequestIds.REQUEST_CODE_RUN_ACCEPT,
                    NotificationConfiguration.RequestIds.REQUEST_CODE_RUN_REJECT,
                    context.getString(R.string.run)
                );
            case AutomaticActivityTypes.CYCLE:
                return new AutomaticActivityStatusInfo(
                    NotificationConfiguration.RequestIds.REQUEST_CODE_CYCLE_ACCEPT,
                    NotificationConfiguration.RequestIds.REQUEST_CODE_CYCLE_REJECT,
                    context.getString(R.string.cycle)
                );
            case AutomaticActivityTypes.VEHICLE:
                return new AutomaticActivityStatusInfo(
                    NotificationConfiguration.RequestIds.REQUEST_CODE_VEHICLE_ACCEPT,
                    NotificationConfiguration.RequestIds.REQUEST_CODE_VEHICLE_REJECT,
                    context.getString(R.string.vehicle)
                );
            default:
                return new AutomaticActivityStatusInfo(
                    NotificationConfiguration.RequestIds.REQUEST_CODE_APP_ACCEPT,
                    NotificationConfiguration.RequestIds.REQUEST_CODE_APP_REJECT,
                    appName
                );
        }
    }
}
