package com.joshuarichardson.fivewaystowellbeing.surveys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;

import java.util.ArrayList;
import java.util.List;

import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.BASIC_SURVEY;

public class SurveyBuilder {

    Context context;

    List<QuestionsToAsk> questions;

    boolean hasBasicSurvey;
    private List<String> basicSurveyQuestions;

    public SurveyBuilder(Context context) {
        this.context = context;
        this.questions = new ArrayList<>();
    }
      // ToDo - perhaps reinstate this with QuestionsToAsk type
//    public SurveyBuilder withQuestion(SurveyQuestion question) {
//        this.questions.add(question);
//        return this;
//    }

    // Take a list of all questions to use
    public SurveyBuilder withQuestions(List<QuestionsToAsk> questionsToAsk) {
        this.questions = questionsToAsk;
        return this;
    }

    public SurveyBuilder withBasicSurvey(List<String> activities) {
        this.basicSurveyQuestions = activities;
        this.hasBasicSurvey = true;
        return this;
    }

    public LinearLayout build() {
        LinearLayout layout = new LinearLayout(this.context);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        int counter = 0;

        for(QuestionsToAsk question : this.questions) {
            switch (SurveyItemTypes.valueOf(question.getType())) {
                case DROP_DOWN_LIST:
                    LayoutInflater inflater = LayoutInflater.from(this.context);
                    View cardView = inflater.inflate(R.layout.input_drop_down, layout, false);

                    cardView.setId(counter);
                    cardView.setTag(SurveyItemTypes.valueOf(question.getType()));

                    TextInputLayout container = cardView.findViewById(R.id.drop_down_container);
                    AutoCompleteTextView dropDownInput = container.findViewById(R.id.drop_down_input);

                    Gson gson = new Gson();
                    DropDownListOptionWrapper optionsList = gson.fromJson(question.extraData, DropDownListOptionWrapper.class);
                    List<String> myQuestions = optionsList.getOptionsList();

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.context, R.layout.item_list_text, myQuestions);
                    dropDownInput.setAdapter(adapter);

                    TextView questionText = cardView.findViewById(R.id.question_title);
                    questionText.setText(question.getQuestion());

                    container.setHint(question.getQuestion());

                    layout.addView(cardView);

                    break;
                case TEXT:
                    LayoutInflater textInflater = LayoutInflater.from(this.context);
                    View textCard = textInflater.inflate(R.layout.input_text, layout, false);

                    textCard.setId(counter);
                    textCard.setTag(SurveyItemTypes.valueOf(question.getType()));

                    TextInputLayout textContainer = textCard.findViewById(R.id.text_input_container);
                    TextView title = textCard.findViewById(R.id.question_title);

                    title.setText(question.getQuestion());

                    textContainer.setHint(question.getQuestion());

                    layout.addView(textCard);
                    break;
                case BASIC_SURVEY:
                    LayoutInflater basicSurveyInflater = LayoutInflater.from(this.context);
                    View basicSurvey = basicSurveyInflater.inflate(R.layout.basic_survey_details, layout, false);
                    basicSurvey.setTag(BASIC_SURVEY);
                    TextInputLayout dropDownContainer = basicSurvey.findViewById(R.id.survey_activity_input_container);
                    AutoCompleteTextView activityDropDownInput = dropDownContainer.findViewById(R.id.survey_activity_input);

                    List<String> basicSurveyQuestion = this.basicSurveyQuestions;

                    ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this.context, R.layout.item_list_text, basicSurveyQuestion);
                    activityDropDownInput.setAdapter(activityAdapter);
                    layout.addView(basicSurvey);
                default:
                    break;
            }
            counter ++;
        }

        return layout;
    }
}
