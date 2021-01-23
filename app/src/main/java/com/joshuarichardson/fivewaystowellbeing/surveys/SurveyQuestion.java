package com.joshuarichardson.fivewaystowellbeing.surveys;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class SurveyQuestion {

    private String questionText = "";
    private List<String> optionsList = new ArrayList<>();
    private SurveyItemTypes questionType;

    public SurveyQuestion(@NonNull String questionText, @NonNull List<String> optionsList, SurveyItemTypes questionType) {
        this.questionText = questionText;
        this.optionsList = optionsList;
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public List<String> getOptionsList() {
        return this.optionsList;
    }

    public SurveyItemTypes getQuestionType() {
        return this.questionType;
    }
}
