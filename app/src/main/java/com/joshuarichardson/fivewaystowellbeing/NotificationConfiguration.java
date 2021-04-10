package com.joshuarichardson.fivewaystowellbeing;

public class NotificationConfiguration {
    // Channels
    public static class ChannelsId {
        public static final String CHANNEL_ID_SURVEYS = "survey_notification_channel";
        public static final String CHANNEL_ID_AUTO_ACTIVITY = "auto_tracking_activity";
        public static final String CHANNEL_ID_APP_USAGE = "app_usage_channel";
    }

    public static class NotificationsId {
        public static final int SURVEY_REMINDER = 4;
        public static final int AUTOMATIC_ACTIVITY_NOTIFICATION_WALK = 5;
        public static final int AUTOMATIC_ACTIVITY_NOTIFICATION_RUN = 6;
        public static final int AUTOMATIC_ACTIVITY_NOTIFICATION_CYCLE = 7;
        public static final int AUTOMATIC_ACTIVITY_NOTIFICATION_VEHICLE = 8;
        public static final int AUTOMATIC_ACTIVITY_NOTIFICATION_APP = 9;
        public static final int APP_USAGE_ID = 10;
    }

    public static class RequestIds {
        public static final int REQUEST_CODE_MAIN_ACTIVITY = 0;

        public static final int REQUEST_CODE_WALK_ACCEPT = 101;
        public static final int REQUEST_CODE_WALK_REJECT = 102;
        public static final int REQUEST_CODE_RUN_ACCEPT = 103;
        public static final int REQUEST_CODE_RUN_REJECT = 104;
        public static final int REQUEST_CODE_CYCLE_ACCEPT = 105;
        public static final int REQUEST_CODE_CYCLE_REJECT = 106;
        public static final int REQUEST_CODE_VEHICLE_ACCEPT = 107;
        public static final int REQUEST_CODE_VEHICLE_REJECT = 108;
        public static final int REQUEST_CODE_APP_ACCEPT = 109;
        public static final int REQUEST_CODE_APP_REJECT = 110;

        public static final int REQUEST_CODE_APP_ASSIGNMENT = 201;
    }
}
