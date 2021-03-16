package com.joshuarichardson.fivewaystowellbeing.storage;

public class ActivityStats {
    private int count;
    long activityId;

    public ActivityStats(long activityId, int count) {
        this.activityId = activityId;
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public long getActivityId() {
        return activityId;
    }
}
