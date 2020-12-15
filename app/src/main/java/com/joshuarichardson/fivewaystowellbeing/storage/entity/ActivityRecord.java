package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// ToDo define the contraints
@Entity(tableName = "activity_records")
public class ActivityRecord {

    // ToDo: define a contract somewhere
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="activity_record_id")
    private Integer activityRecordId;

    @ColumnInfo(name = "activity_name")
    private String activityName;

    @ColumnInfo(name="activity_type")
    private String activityType;

    @ColumnInfo(name = "activity_timestamp")
    private int activityTimestamp;

    @ColumnInfo(name = "activity_duration")
    private int activityDuration;

    @ColumnInfo(name = "activity_survey_id")
    private int activitySurveyId;

    public ActivityRecord(String activityName, int activityDuration, int activityTimestamp, String activityType, int activitySurveyId) {
        this.setActivityName(activityName);
        this.setActivityDuration(activityDuration);
        this.setActivityTimestamp(activityTimestamp);
        this.setActivityType(activityType);
        this.setActivitySurveyId(activitySurveyId);
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

    public void setActivitySurveyId(int surveyId) {
        this.activitySurveyId = surveyId;
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

    public int getActivitySurveyId() {
        return this.activitySurveyId;
    }

    public int getActivityTimestamp() {
        return this.activityTimestamp;
    }
}
