package com.joshuarichardson.fivewaystowellbeing;

/**
 * Class for notification info
 */
public class NotificationInfo {
    private int iconId;
    private String title;
    private int notificationId;

    public NotificationInfo(int iconId, String title, int notificationId) {
        this.iconId = iconId;
        this.title = title;
        this.notificationId = notificationId;
    }

    public int getIconId() {
        return this.iconId;
    }

    public int getNotificationId() {
        return this.notificationId;
    }

    public String getTitle() {
        return this.title;
    }
}
