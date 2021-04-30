package com.joshuarichardson.fivewaystowellbeing.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Calendar;

import javax.inject.Inject;

import androidx.preference.PreferenceManager;

/**
 * A class of helper functions for scheduling alarms to trigger notifications
 */
public class AlarmHelper {

    @Inject
    public AlarmHelper() {}

    /**
     * Calculate the times and then schedule the notification
     *
     * @param context The application context
     * @param timeOfDay The name of the time of day to schedule the notification
     */
    public void scheduleNotification(Context context, String timeOfDay) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int hours = 0;
        int minutes = 0;

        // Get the time and whether it is enabled
        boolean isEnabled = sharedPreferences.getBoolean("notification_" + timeOfDay + "_switch", true);
        long time = sharedPreferences.getLong("notification_" + timeOfDay + "_time", -1);

        if(time == -1) {
            return;
        }

        // If there is a time set, find out the hours and minutes
        if(time > 0) {
            hours = (int) time / 60 / 60 / 1000;
            minutes = (int) (time / 60 / 1000) - (hours * 60);
        }

        scheduleNotification(context, hours, minutes, timeOfDay, isEnabled);
    }

    /**
     * Use alarm manager to schedule a notification using a pending intent
     *
     * @param context The application context
     * @param hour The hour to schedule the notification for
     * @param minutes The minute to schedule the notification for
     * @param timeOfDay The notification identifier
     * @param isEnabled The enabled state of the notification
     */
    public void scheduleNotification(Context context, int hour, int minutes, String timeOfDay, boolean isEnabled) {
        // Define which broadcast receiver to trigger
        long timeToSchedule = getTimeToSchedule(hour, minutes);
        Intent notificationIntent = new Intent(context, SendSurveyReminderNotificationBroadcastReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putString("time", timeOfDay);
        notificationIntent.putExtras(bundle);

        // Pending intents should have a unique number
        int notificationNumber;
        if(timeOfDay.equals("morning")) {
            notificationNumber = 1;
        }
        else if(timeOfDay.equals("noon")) {
            notificationNumber = 2;
        } else {
            notificationNumber = 3;
        }

        // Create the pending intent regardless of whether enabled or not
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationNumber, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isEnabled) {
            // Set an exact time for notification = even while app is closed
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, timeToSchedule, pendingIntent);
        } else {
            // Cancel if not enabled by user
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * Schedule a time based on hours and minutes
     *
     * @param hour The hour of day
     * @param minute The minute of day
     * @return The millisecond timestamp to schedule the notification
     */
    public static long getTimeToSchedule(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Get the current time before setting the time requested
        long startTime = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Add 1 to the day if the time for today has passed already
        if(startTime > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        return calendar.getTimeInMillis();
    }
}