package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "survey_response")
public class SurveyResponse {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int surveyResponseId;

    @NonNull
    @ColumnInfo(name = "timestamp")
    private int surveyResponseTimestamp;

    @ColumnInfo(name = "way_to_wellbeing")
    private String surveyResponseWayToWellbeing;

    public SurveyResponse(int surveyResponseTimestamp, String surveyResponseWayToWellbeing) {
        this.setSurveyResponseTimestamp(surveyResponseTimestamp);
        this.setSurveyResponseWayToWellbeing(surveyResponseWayToWellbeing);
    }

    public void setSurveyResponseId(int surveyResponseId) {
        this.surveyResponseId = surveyResponseId;
    }

    public void setSurveyResponseTimestamp(int surveyResponseTimestamp) {
        this.surveyResponseTimestamp = surveyResponseTimestamp;
    }

    public void setSurveyResponseWayToWellbeing(String surveyResponseWayToWellbeing) {
        this.surveyResponseWayToWellbeing = surveyResponseWayToWellbeing;
    }

    public int getSurveyResponseId() {
        return surveyResponseId;
    }

    public int getSurveyResponseTimestamp() {
        return surveyResponseTimestamp;
    }

    public String getSurveyResponseWayToWellbeing() {
        return surveyResponseWayToWellbeing;
    }
}
