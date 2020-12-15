package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "survey_response")
public class SurveyResponse {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int surveyResponseId;

    public void setSurveyResponseId(int surveyResponseId) {
        this.surveyResponseId = surveyResponseId;
    }

    public int getSurveyResponseId() {
        return surveyResponseId;
    }
}
