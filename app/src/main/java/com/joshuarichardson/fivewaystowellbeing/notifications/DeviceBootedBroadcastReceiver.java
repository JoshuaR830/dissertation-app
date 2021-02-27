package com.joshuarichardson.fivewaystowellbeing.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// References:
// https://stackoverflow.com/questions/12034357/does-alarm-manager-persist-even-after-reboot
public class DeviceBootedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmHelper helper = AlarmHelper.getInstance();

        // Schedule all of the alarms for their correct time
        helper.scheduleNotification(context, "morning");
        helper.scheduleNotification(context, "noon");
        helper.scheduleNotification(context, "night");
    }
}
