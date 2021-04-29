package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.AUTOMATIC_ACTIVITY_ACTIVITY_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.AUTOMATIC_ACTIVITY_END_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.AUTOMATIC_ACTIVITY_IS_CONFIRMED;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.AUTOMATIC_ACTIVITY_IS_PENDING;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.AUTOMATIC_ACTIVITY_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.AUTOMATIC_ACTIVITY_START_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.AUTOMATIC_ACTIVITY_TABLE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.AUTOMATIC_ACTIVITY_TYPE;

/**
 * The entity representing the activities that can be logged automatically
 * The table is used to hold the real life activities that can be suggested based on phone usage
 */
@Entity(tableName = AUTOMATIC_ACTIVITY_TABLE)
public class AutomaticActivity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = AUTOMATIC_ACTIVITY_TYPE)
    private String activityType;

    @ColumnInfo(name = AUTOMATIC_ACTIVITY_NAME)
    private String name;

    @ColumnInfo(name = AUTOMATIC_ACTIVITY_START_TIME)
    private long startTime;

    @ColumnInfo(name = AUTOMATIC_ACTIVITY_END_TIME)
    private long endTime;

    @ColumnInfo(name = AUTOMATIC_ACTIVITY_ACTIVITY_ID)
    private long activityId;

    @ColumnInfo(name = AUTOMATIC_ACTIVITY_IS_PENDING)
    private boolean isPending;

    @ColumnInfo(name = AUTOMATIC_ACTIVITY_IS_CONFIRMED)
    private boolean isNotificationConfirmed;

    public AutomaticActivity(String activityType, @Nullable String name, long startTime, long endTime, long activityId, boolean isPending, boolean isNotificationConfirmed) {
        this.activityType = activityType;
        this.name = name;
        this.startTime = startTime;
        this.activityId = activityId;
        this.isPending = isPending;
        this.endTime = endTime;
        this.isNotificationConfirmed = isNotificationConfirmed;
    }

    public long getActivityId() {
        return this.activityId;
    }

    public String getName() {
        return name;
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