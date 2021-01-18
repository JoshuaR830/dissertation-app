package com.joshuarichardson.fivewaystowellbeing.surveys;

import android.content.Context;
import android.view.View;

public class SurveyBuilder {

    Context context;

    public SurveyBuilder(Context context) {
        this.context = context;
    }

    public SurveyBuilder withQuestion(SurveyQuestion question) {
        return this;
    }

    public View build() {
        return new View(this.context);
    }


}
