package com.joshuarichardson.fivewaystowellbeing.surveys;

import java.util.ArrayList;
import java.util.List;

public class QuestionBuilder {

    private String questionText = "";
    private List<String> optionsList = new ArrayList<>();
    private SurveyItemTypes itemType;


    public QuestionBuilder withQuestionText(String questionText) {
        this.questionText = questionText;
        return this;
    }

    public QuestionBuilder withOptionsList(List<String> optionsList) {
        this.optionsList = optionsList;
        return this;
    }

    public QuestionBuilder withType(SurveyItemTypes itemType) {
        this.itemType = itemType;
        return this;
    }

    public SurveyQuestion build() {
        return new SurveyQuestion(this.questionText, this.optionsList, this.itemType);
    }
}
