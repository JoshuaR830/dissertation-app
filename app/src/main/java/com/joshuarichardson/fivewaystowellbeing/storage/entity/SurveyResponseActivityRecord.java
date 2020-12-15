package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "survey_activity", primaryKeys = {"activity_record_id", "survey_response_id"})
public class SurveyResponseActivityRecord {

    @NonNull
    @ColumnInfo(name = "activity_record_id")
    private int activityRecordId;

    @NonNull
    @ColumnInfo(name  = "survey_response_id")
    private int surveyResponseId;

    public SurveyResponseActivityRecord(int surveyResponseId, int activityRecordId) {
        setSurveyResponseId(surveyResponseId);
        setActivityRecordId(activityRecordId);
    }

    public void setActivityRecordId(int activityRecordId) {
        this.activityRecordId = activityRecordId;
    }

    public void setSurveyResponseId(int surveyResponseId) {
        this.surveyResponseId = surveyResponseId;
    }

    public int getActivityRecordId() {
        return this.activityRecordId;
    }

    public int getSurveyResponseId() {
        return this.surveyResponseId;
    }
}
