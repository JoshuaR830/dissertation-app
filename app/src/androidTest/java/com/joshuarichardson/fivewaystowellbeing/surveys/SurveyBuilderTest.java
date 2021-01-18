package com.joshuarichardson.fivewaystowellbeing.surveys;

import android.app.Activity;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.joshuarichardson.fivewaystowellbeing.AnswerSurveyActivity;
import com.joshuarichardson.fivewaystowellbeing.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static com.google.common.truth.Truth.assertThat;
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.DROP_DOWN_LIST;

public class SurveyBuilderTest {

    private View surveyBuilder;
    private SurveyQuestion question;

    Activity currentActivity = null;


    @Rule
    public ActivityScenarioRule<AnswerSurveyActivity> answerSurveyActivity = new ActivityScenarioRule<>(AnswerSurveyActivity.class);

    @Before
    public void setUp() {
//        Context context = getInstrumentation().getContext();
        String[] feelings = new String[]{"Happy", "Moderate", "Sad"};

        this.question = new QuestionBuilder()
            .withQuestionText("How are you feeling?")
            .withOptionsList(Arrays.asList(feelings))
            .withType(DROP_DOWN_LIST)
            .build();

//        AnswerSurveyActivity activity = (AnswerSurveyActivity) getActivityInstance();

//        if(activity == null) {
//            throw new IllegalArgumentException();
//        }

//        View view = activity.findViewById(R.layout.activity_answer_survey);
//
//        this.surveyBuilder = new SurveyBuilder(context)
//            .withQuestion(this.question)
//            .build();


//        answerSurveyActivity.getScenario().onActivity(
//                (activity) -> {
//                    LinearLayout layout = activity.findViewById(R.id.survey_items_layout);
////                    activity.findViewById(R.id.survey_items_layout);
//                }
//        );
//         https://stackoverflow.com/a/56356650/13496270
        answerSurveyActivity.getScenario().onActivity(
                (activity) -> {
                    this.surveyBuilder = new SurveyBuilder(activity)
                    .withQuestion(this.question)
                    .build();
                }
        );
    }

    @After
    public void tearDown() {
        this.question = null;
        this.surveyBuilder = null;
    }

    @Test
    public void surveyBuilder_ShouldReturnViewWithItems() throws IllegalArgumentException {
        // Check the view exists
        View view = this.surveyBuilder
//            .requireViewById(R.layout.activity_answer_survey)
            .requireViewById(0)
            .requireViewById(R.id.drop_down_container);

        assertThat(view).isInstanceOf(com.google.android.material.textfield.TextInputLayout.class);

        View dropDown = view.requireViewById(R.id.drop_down_input);

        // Check that the type is an auto complete text type as that is used for drop down lists
        assertThat(dropDown).isInstanceOf(AutoCompleteTextView.class);

        assertThat(((com.google.android.material.textfield.TextInputLayout)view).getHint()).isEqualTo("How are you feeling?");
    }

    @Test
    public void whenQuestionBuilderIsSuppliedWithAllValues_ACompleteQuestionShouldBeReturned() {

        // Check the question text matches
        assertThat(this.question.getQuestionText()).isEqualTo("How are you feeling?");

        // Check the type matches
        assertThat(this.question.getQuestionType()).isEqualTo(DROP_DOWN_LIST);

        // Check the question list matches
        assertThat(this.question.getOptionsList()).isNotNull();
        assertThat(this.question.getOptionsList()).isNotEmpty();
        assertThat(this.question.getOptionsList()).hasSize(3);

        assertThat(this.question.getOptionsList().get(0)).isEqualTo("Happy");
        assertThat(this.question.getOptionsList().get(1)).isEqualTo("Moderate");
        assertThat(this.question.getOptionsList().get(2)).isEqualTo("Sad");
    }

    @Test
    public void whenQuestionBuilderIsSuppliedWithoutValues_ThenDefaultValuesShouldBeReturned() {

        // Check the question text matches
        assertThat(this.question.getQuestionText()).isEqualTo("");

        // Check the type matches
        assertThat(this.question.getQuestionType()).isEqualTo(null);

        // Check the question list matches
        assertThat(this.question.getOptionsList()).isNotNull();
        assertThat(this.question.getOptionsList()).isEmpty();
    }
}
