package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activity_records")
public class ActivityRecord {

    // ToDo: define a contract somewhere
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    private Integer activityRecordId;

    @NonNull
    @ColumnInfo(name = "name")
    private String activityName;

    @ColumnInfo(name="type")
    private String activityType;

    @NonNull
    @ColumnInfo(name = "timestamp")
    private int activityTimestamp;

    @NonNull
    @ColumnInfo(name = "duration")
    private int activityDuration;

    public ActivityRecord(String activityName, int activityDuration, int activityTimestamp, String activityType) {
        this.setActivityName(activityName);
        this.setActivityDuration(activityDuration);
        this.setActivityTimestamp(activityTimestamp);
        this.setActivityType(activityType);
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

    public void setActivityTimestamp(int timestamp) {
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

    public int getActivityTimestamp() {
        return this.activityTimestamp;
    }
}
