package com.joshuarichardson.fivewaystowellbeing.surveys;

import java.util.List;

public class QuestionBuilder {

    public QuestionBuilder withQuestionText(String s) {
        return this;
    }

    public QuestionBuilder withOptionsList(List<String> asList) {
        return this;
    }

    public QuestionBuilder withType(SurveyItemTypes dropDownList) {
        return this;
    }

    public SurveyQuestion build() {
        return new SurveyQuestion();
    }
}
