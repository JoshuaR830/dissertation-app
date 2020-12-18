package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ELEMENT_ANSWER;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ELEMENT_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ELEMENT_QUESTION;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ELEMENT_SURVEY_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ELEMENT_TABLE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.SURVEY_RESPONSE_ID;

@Entity(tableName = SURVEY_RESPONSE_ELEMENT_TABLE_NAME)
public class SurveyResponseElement {

    @ColumnInfo(name = SURVEY_RESPONSE_ELEMENT_ID)
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = SURVEY_RESPONSE_ELEMENT_QUESTION)
    private String question;

    @ColumnInfo(name = SURVEY_RESPONSE_ELEMENT_ANSWER)
    private String answer;

    @ColumnInfo(name= SURVEY_RESPONSE_ELEMENT_SURVEY_ID)
    @ForeignKey(entity = SurveyResponse.class, parentColumns = SURVEY_RESPONSE_ID, childColumns = SURVEY_RESPONSE_ELEMENT_SURVEY_ID, onDelete = CASCADE)
    private int surveyId;

    public SurveyResponseElement(int surveyId, String question, String answer) {
        this.setAnswer(answer);
        this.setSurveyId(surveyId);
        this.setQuestion(question);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public String getQuestion() {
        return question;
    }
}
