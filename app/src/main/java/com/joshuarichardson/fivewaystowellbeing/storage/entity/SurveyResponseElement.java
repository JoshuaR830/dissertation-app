package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "survey_response_element")
public class SurveyResponseElement {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "question")
    private String question;

    @ColumnInfo(name = "answer")
    private String answer;

    @ColumnInfo(name="survey_id")
    @ForeignKey(entity = SurveyResponse.class, parentColumns = "id", childColumns = "survey_id", onDelete = CASCADE)
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
