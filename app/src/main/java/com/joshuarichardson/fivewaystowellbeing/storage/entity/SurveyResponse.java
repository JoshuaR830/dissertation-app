package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_BE_ACTIVE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_CONNECT;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_DESCRIPTION;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_GIVE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_KEEP_LEARNING;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_TAKE_NOTICE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_TIMESTAMP;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_TITLE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_WAY_TO_WELLBEING;

@Entity(tableName = SURVEY_RESPONSE_TABLE_NAME)
public class SurveyResponse {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SURVEY_RESPONSE_ID)
    private long surveyResponseId;

    @NonNull
    @ColumnInfo(name = SURVEY_RESPONSE_TIMESTAMP)
    private long surveyResponseTimestamp;

    @ColumnInfo(name = SURVEY_RESPONSE_WAY_TO_WELLBEING)
    private String surveyResponseWayToWellbeing;

    @ColumnInfo(name = SURVEY_RESPONSE_TITLE)
    private String title;

    @ColumnInfo(name = SURVEY_RESPONSE_DESCRIPTION)
    private String description;

    @ColumnInfo(name = SURVEY_RESPONSE_CONNECT)
    private int connectValue;

    @ColumnInfo(name = SURVEY_RESPONSE_BE_ACTIVE)
    private int beActiveValue;

    @ColumnInfo(name = SURVEY_RESPONSE_KEEP_LEARNING)
    private int keepLearningValue;

    @ColumnInfo(name = SURVEY_RESPONSE_TAKE_NOTICE)
    private int takeNoticeValue;

    @ColumnInfo(name = SURVEY_RESPONSE_GIVE)
    private int giveValue;

    public SurveyResponse(long surveyResponseTimestamp, String surveyResponseWayToWellbeing, String title, String description, int connectValue, int beActiveValue, int keepLearningValue, int takeNoticeValue, int giveValue) {
        this.setSurveyResponseTimestamp(surveyResponseTimestamp);
        this.setSurveyResponseWayToWellbeing(surveyResponseWayToWellbeing);
        this.setTitle(title);
        this.setDescription(description);
        this.setConnectValue(connectValue);
        this.setBeActiveValue(beActiveValue);
        this.setKeepLearningValue(keepLearningValue);
        this.setTakeNoticeValue(takeNoticeValue);
        this.setGiveValue(giveValue);
    }

    public SurveyResponse(long surveyResponseTimestamp, WaysToWellbeing surveyResponseWayToWellbeing, String title, String description, int connectValue, int beActiveValue, int keepLearningValue, int takeNoticeValue, int giveValue) {
        this.setSurveyResponseTimestamp(surveyResponseTimestamp);
        this.setSurveyResponseWayToWellbeing(surveyResponseWayToWellbeing.name());
        this.setTitle(title);
        this.setDescription(description);
        this.setConnectValue(connectValue);
        this.setBeActiveValue(beActiveValue);
        this.setKeepLearningValue(keepLearningValue);
        this.setTakeNoticeValue(takeNoticeValue);
        this.setGiveValue(giveValue);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setSurveyResponseId(long surveyResponseId) {
        this.surveyResponseId = surveyResponseId;
    }

    public void setSurveyResponseTimestamp(long surveyResponseTimestamp) {
        this.surveyResponseTimestamp = surveyResponseTimestamp;
    }

    public void setSurveyResponseWayToWellbeing(String surveyResponseWayToWellbeing) {
        this.surveyResponseWayToWellbeing = surveyResponseWayToWellbeing;
    }

    public void setConnectValue(int connectValue) {
        this.connectValue = connectValue;
    }

    public void setBeActiveValue(int beActiveValue) {
        this.beActiveValue = beActiveValue;
    }

    public void setKeepLearningValue(int keepLearningValue) {
        this.keepLearningValue = keepLearningValue;
    }

    public void setTakeNoticeValue(int takeNoticeValue) {
        this.takeNoticeValue = takeNoticeValue;
    }

    public void setGiveValue(int giveValue) {
        this.giveValue = giveValue;
    }

    public long getSurveyResponseId() {
        return this.surveyResponseId;
    }

    public long getSurveyResponseTimestamp() {
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

    public int getConnectValue() {
        return this.connectValue;
    }

    public int getBeActiveValue() {
        return this.beActiveValue;
    }

    public int getKeepLearningValue() {
        return this.keepLearningValue;
    }

    public int getTakeNoticeValue() {
        return this.takeNoticeValue;
    }

    public int getGiveValue() {
        return this.giveValue;
    }
}
