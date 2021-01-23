package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.ACTIVITY_RECORD_DURATION;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.ACTIVITY_RECORD_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.ACTIVITY_RECORD_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.ACTIVITY_RECORD_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.ACTIVITY_RECORD_TIMESTAMP;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.ACTIVITY_RECORD_TYPE;

@Entity(tableName = ACTIVITY_RECORD_TABLE_NAME)
public class ActivityRecord {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ACTIVITY_RECORD_ID)
    private Integer activityRecordId;

    @NonNull
    @ColumnInfo(name = ACTIVITY_RECORD_NAME)
    private String activityName;

    @ColumnInfo(name = ACTIVITY_RECORD_TYPE)
    private String activityType;

    @NonNull
    @ColumnInfo(name = ACTIVITY_RECORD_TIMESTAMP)
    private long activityTimestamp;

    @NonNull
    @ColumnInfo(name = ACTIVITY_RECORD_DURATION)
    private int activityDuration;

    public ActivityRecord(String activityName, int activityDuration, long activityTimestamp, String activityType) {
        this.setActivityName(activityName);
        this.setActivityDuration(activityDuration);
        this.setActivityTimestamp(activityTimestamp);
        this.setActivityType(activityType);
    }

    public ActivityRecord(String activityName, int activityDuration, long activityTimestamp, ActivityType activityType) {
        this.setActivityName(activityName);
        this.setActivityDuration(activityDuration);
        this.setActivityTimestamp(activityTimestamp);
        this.setActivityType(activityType.name());
    }

    public void setActivityRecordId(@NonNull Integer recordId) {
        this.activityRecordId = recordId;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public void setActivityType(String type) {
        this.activityType = type;
    }

    public void setActivityDuration(int duration) {
        this.activityDuration = duration;
    }

    public void setActivityTimestamp(long timestamp) {
        this.activityTimestamp = timestamp;
    }

    @NonNull
    public Integer getActivityRecordId() {
        return activityRecordId;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getActivityType() {
        return activityType;
    }

    public int getActivityDuration() {
        return this.activityDuration;
    }

    public long getActivityTimestamp() {
        return this.activityTimestamp;
    }
}
