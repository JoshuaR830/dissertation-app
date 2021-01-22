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

    boolean hasBasicSurvey;

    public SurveyBuilder(Context context) {
        this.context = context;
        this.questions = new ArrayList<>();
    }

    public SurveyBuilder withQuestion(SurveyQuestion question) {
        this.questions.add(question);
        return this;
    }

    public SurveyBuilder withBasicSurvey() {
        this.hasBasicSurvey = true;
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

        if(hasBasicSurvey) {
            Log.d("Basic Survey", "Yes");
        }

        for(SurveyQuestion question : this.questions) {
            switch (question.getQuestionType()) {
                case DROP_DOWN_LIST:
                    LayoutInflater inflater = LayoutInflater.from(this.context);
                    View cardView = inflater.inflate(R.layout.input_drop_down, layout, false);

                    cardView.setId(counter);
                    cardView.setTag(question.getQuestionType());

                    TextInputLayout container = cardView.findViewById(R.id.drop_down_container);
                    AutoCompleteTextView dropDownInput = container.findViewById(R.id.drop_down_input);
                    List<String> myQuestions = question.getOptionsList();

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.context, R.layout.item_list_text, myQuestions);
                    dropDownInput.setAdapter(adapter);

                    TextView questionText = cardView.findViewById(R.id.question_title);
                    questionText.setText(question.getQuestionText());

                    container.setHint(question.getQuestionText());

                    layout.addView(cardView);

                    break;
                case TEXT:
                    LayoutInflater textInflater = LayoutInflater.from(this.context);
                    View textCard = textInflater.inflate(R.layout.input_text, layout, false);

                    textCard.setId(counter);
                    textCard.setTag(question.getQuestionType());

                    TextInputLayout textContainer = textCard.findViewById(R.id.text_input_container);
                    TextView title = textCard.findViewById(R.id.question_title);

                    title.setText(question.getQuestionText());

                    textContainer.setHint(question.getQuestionText());

                    layout.addView(textCard);
                default:
                    break;
            }
            counter ++;
        }

        return layout;
    }
}
