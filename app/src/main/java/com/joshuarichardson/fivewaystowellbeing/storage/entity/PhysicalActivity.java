package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.PHYSICAL_ACTIVITY_ACTIVITY_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.PHYSICAL_ACTIVITY_END_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.PHYSICAL_ACTIVITY_IS_CONFIRMED;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.PHYSICAL_ACTIVITY_IS_PENDING;
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

    @ColumnInfo(name = PHYSICAL_ACTIVITY_END_TIME)
    private long endTime;

    @ColumnInfo(name = PHYSICAL_ACTIVITY_ACTIVITY_ID)
    private long activityId;

    @ColumnInfo(name = PHYSICAL_ACTIVITY_IS_PENDING)
    private boolean isPending;

    @ColumnInfo(name = PHYSICAL_ACTIVITY_IS_CONFIRMED)
    private boolean isNotificationConfirmed;

    public PhysicalActivity(String activityType, long startTime, long endTime, long activityId, boolean isPending, boolean isNotificationConfirmed) {
        this.activityType = activityType;
        this.startTime = startTime;
        this.activityId = activityId;
        this.isPending = isPending;
        this.endTime = endTime;
        this.isNotificationConfirmed = isNotificationConfirmed;
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

    public long getEndTime() {
        return endTime;
    }

    public boolean isPending() {
        return this.isPending;
    }

    public boolean getTimeStatus(long endTime) {
        int tenMins = 600000;
        return endTime >= (startTime + tenMins);
    }

    public boolean isNotificationConfirmed() {
        return this.isNotificationConfirmed;
    }
}