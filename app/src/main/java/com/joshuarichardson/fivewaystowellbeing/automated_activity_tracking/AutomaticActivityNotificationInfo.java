package com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking;

import com.joshuarichardson.fivewaystowellbeing.NotificationInfo;

/**
 * Contains information about a notification
 */
public class AutomaticActivityNotificationInfo extends NotificationInfo {
    private final String activityName;

    public AutomaticActivityNotificationInfo(String activityName, String title, int iconId, int notificationId) {
        super(iconId, title, notificationId);
        this.activityName = activityName;
    }

    public String getActivityName() {
        return this.activityName;
    }
}
