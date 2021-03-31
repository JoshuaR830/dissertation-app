package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.PHYSICAL_ACTIVITY_ACTIVITY_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.PHYSICAL_ACTIVITY_START_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.PHYSICAL_ACTIVITY_TABLE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.PHYSICAL_ACTIVITY_TYPE;

@Entity(tableName = PHYSICAL_ACTIVITY_TABLE)
public class PhysicalActivity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = PHYSICAL_ACTIVITY_TYPE)
    private String activityType;

    @ColumnInfo(name = PHYSICAL_ACTIVITY_START_TIME)
    private long startTime;

    @ColumnInfo(name = PHYSICAL_ACTIVITY_ACTIVITY_ID)
    private long activityId;

    public PhysicalActivity(String activityType, long startTime, long activityId) {
        this.activityType = activityType;
        this.startTime = startTime;
        this.activityId = activityId;
    }

    public long getActivityId() {
        return this.activityId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public String getActivityType() {
        return this.activityType;
    }

    public boolean getTimeStatus(int endTime) {
        int tenMins = 600000;
        return endTime >= (startTime + tenMins);

    }
}