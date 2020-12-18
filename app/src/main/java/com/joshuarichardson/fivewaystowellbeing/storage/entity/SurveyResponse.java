package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_TIMESTAMP;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_WAY_TO_WELLBEING;

@Entity(tableName = SURVEY_RESPONSE_TABLE_NAME)
public class SurveyResponse {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SURVEY_RESPONSE_ID)
    private int surveyResponseId;

    @NonNull
    @ColumnInfo(name = SURVEY_RESPONSE_TIMESTAMP)
    private int surveyResponseTimestamp;

    @ColumnInfo(name = SURVEY_RESPONSE_WAY_TO_WELLBEING)
    private String surveyResponseWayToWellbeing;

    public SurveyResponse(int surveyResponseTimestamp, String surveyResponseWayToWellbeing) {
        this.setSurveyResponseTimestamp(surveyResponseTimestamp);
        this.setSurveyResponseWayToWellbeing(surveyResponseWayToWellbeing);
    }

    public SurveyResponse(int surveyResponseTimestamp, WaysToWellbeing surveyResponseWayToWellbeing) {
        this.setSurveyResponseTimestamp(surveyResponseTimestamp);
        this.setSurveyResponseWayToWellbeing(surveyResponseWayToWellbeing.name());
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
