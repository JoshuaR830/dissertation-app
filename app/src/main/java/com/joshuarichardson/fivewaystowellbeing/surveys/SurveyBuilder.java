package com.joshuarichardson.fivewaystowellbeing.surveys;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.ArrayList;
import java.util.List;

public class SurveyBuilder {

    Context context;

    List<SurveyQuestion> questions;

    public SurveyBuilder(Context context) {
        this.context = context;
        this.questions = new ArrayList<>();
    }

    public SurveyBuilder withQuestion(SurveyQuestion question) {
        this.questions.add(question);
        return this;
    }

    public LinearLayout build() {

        // ToDo work out how to use the actual one

//        LinearLayout layout = this.activityViewSurveys.findViewById(R.id.survey_items_layout);
//        view = new View(this.context);

        LinearLayout layout = new LinearLayout(this.context);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Log.d("Hello", "Let's see what happens");

        int counter = 0;

        for(SurveyQuestion question : this.questions) {
            switch (question.getQuestionType()) {
                case DROP_DOWN_LIST:
                    Log.d("Hello", "Drop down list");
                    LayoutInflater inflater = LayoutInflater.from(this.context);
                    View cardView = inflater.inflate(R.layout.input_drop_down, null, false);

                    cardView.setId(counter);

                    AutoCompleteTextView dropDownInput = cardView.findViewById(R.id.drop_down_input);

                    List<String> questions = question.getOptionsList();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.context, R.layout.item_list_text, questions);
                    dropDownInput.setAdapter(adapter);

                    TextView questionText = cardView.findViewById(R.id.question_title);
                    questionText.setText(question.getQuestionText());

                    TextInputLayout container = cardView.findViewById(R.id.drop_down_container);
                    container.setHint(question.getQuestionText());

                    layout.addView(cardView);
                    Log.d("Added", "Added the view");
                    break;
                default:
                    break;
            }
            counter ++;
        }

        return layout;
    }

}
