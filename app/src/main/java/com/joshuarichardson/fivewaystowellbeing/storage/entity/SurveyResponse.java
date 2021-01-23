package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_DESCRIPTION;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_TIMESTAMP;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_TITLE;
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

    @ColumnInfo(name = SURVEY_RESPONSE_TITLE)
    private String title;

    @ColumnInfo(name = SURVEY_RESPONSE_DESCRIPTION)
    private String description;

    public SurveyResponse(int surveyResponseTimestamp, String surveyResponseWayToWellbeing, String title, String description) {
        this.setSurveyResponseTimestamp(surveyResponseTimestamp);
        this.setSurveyResponseWayToWellbeing(surveyResponseWayToWellbeing);
        this.setTitle(title);
        this.setDescription(description);
    }

    public SurveyResponse(int surveyResponseTimestamp, WaysToWellbeing surveyResponseWayToWellbeing, String title, String description) {
        this.setSurveyResponseTimestamp(surveyResponseTimestamp);
        this.setSurveyResponseWayToWellbeing(surveyResponseWayToWellbeing.name());
        this.setTitle(title);
        this.setDescription(description);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return this.surveyResponseId;
    }

    public int getSurveyResponseTimestamp() {
        return this.surveyResponseTimestamp;
    }

    public String getSurveyResponseWayToWellbeing() {
        return this.surveyResponseWayToWellbeing;
    }

    public String getTitle() {
        return this.title;
    }


    public String getDescription() {
        return this.description;
    }
}
