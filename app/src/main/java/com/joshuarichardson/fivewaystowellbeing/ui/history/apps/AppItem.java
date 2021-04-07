package com.joshuarichardson.fivewaystowellbeing.ui.history.apps;

public class AppItem {
    private String packageName;
    private String appName;
    private String activityName;
    private String wayToWellbeing;

    public AppItem(String packageName, String appName, String activityName, String wayToWellbeing) {
        this.packageName = packageName;
        this.appName = appName;
        this.activityName = activityName;
        this.wayToWellbeing = wayToWellbeing;
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
}
