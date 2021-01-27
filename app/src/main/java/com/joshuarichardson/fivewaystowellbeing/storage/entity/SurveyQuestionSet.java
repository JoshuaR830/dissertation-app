package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_QUESTION_SET_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_QUESTION_SET_SURVEY_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_QUESTION_SET_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_QUESTION_SET_TIMESTAMP;

@Entity(tableName = SURVEY_QUESTION_SET_TABLE_NAME)
public class SurveyQuestionSet {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SURVEY_QUESTION_SET_ID)
    public long id;

    @ColumnInfo(name = SURVEY_QUESTION_SET_TIMESTAMP)
    public long timestamp;

    @ColumnInfo(name = SURVEY_QUESTION_SET_SURVEY_ID)
    public long surveyId;

    public SurveyQuestionSet(long timestamp, long surveyId) {
        this.timestamp = timestamp;
        this.surveyId = surveyId;
    }

    public long getId() {
        return this.id;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public long getSurveyId() {
        return this.surveyId;
    }
}
