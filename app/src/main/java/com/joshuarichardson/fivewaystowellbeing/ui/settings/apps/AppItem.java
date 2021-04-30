package com.joshuarichardson.fivewaystowellbeing.ui.settings.apps;

/**
 * The data about an app including assigned activity
 */
public class AppItem {
    private long activityId;
    private String packageName;
    private String appName;
    private String activityName;
    private String wayToWellbeing;

    public AppItem(String packageName, String appName, String activityName, String wayToWellbeing, long activityId) {
        this.packageName = packageName;
        this.appName = appName;
        this.activityName = activityName;
        this.wayToWellbeing = wayToWellbeing;
        this.activityId = activityId;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getWayToWellbeing() {
        return this.wayToWellbeing;
    }

    public long getActivityId() {
        return this.activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
