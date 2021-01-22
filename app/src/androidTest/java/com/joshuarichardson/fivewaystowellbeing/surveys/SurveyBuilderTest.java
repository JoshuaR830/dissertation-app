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
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static com.google.common.truth.Truth.assertThat;
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.DROP_DOWN_LIST;

@HiltAndroidTest
public class SurveyBuilderTest {

    private View surveyBuilder;
    private SurveyQuestion firstQuestion;
    private SurveyQuestion secondQuestion;

    Activity currentActivity = null;

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule
    public ActivityScenarioRule<AnswerSurveyActivity> answerSurveyActivity = new ActivityScenarioRule<>(AnswerSurveyActivity.class);

    @Before
    public void setUp() {
//        Context context = getInstrumentation().getContext();
        String[] feelings = new String[]{"Happy", "Moderate", "Sad"};
        String[] apps = new String[]{"Facebook", "Snapchat", "Whatsapp"};

        this.firstQuestion = new QuestionBuilder()
            .withQuestionText("How are you feeling?")
            .withOptionsList(Arrays.asList(feelings))
            .withType(DROP_DOWN_LIST)
            .build();

        this.secondQuestion = new QuestionBuilder()
            .withQuestionText("What activity have you been doing?")
            .withOptionsList(Arrays.asList(apps))
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
                    .withQuestion(this.firstQuestion)
                    .withQuestion(this.secondQuestion)
                    .build();
                }
        );
    }

    @After
    public void tearDown() {
        this.firstQuestion = null;
        this.secondQuestion = null;
        this.surveyBuilder = null;
    }

    @Test
    public void surveyBuilder_ShouldReturnViewWithItem() throws IllegalArgumentException {
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
    public void surveyBuilder_ShouldReturnAViewWithMultipleItems() {
        View view1 = this.surveyBuilder.requireViewById(0).requireViewById(R.id.drop_down_container);
        View view2 = this.surveyBuilder.requireViewById(1).requireViewById(R.id.drop_down_container);
        assertThat(view1).isInstanceOf(com.google.android.material.textfield.TextInputLayout.class);
        assertThat(view2).isInstanceOf(com.google.android.material.textfield.TextInputLayout.class);

        View dropDown1 = view1.requireViewById(R.id.drop_down_input);
        View dropDown2 = view2.requireViewById(R.id.drop_down_input);

        assertThat(dropDown1).isInstanceOf(AutoCompleteTextView.class);
        assertThat(dropDown2).isInstanceOf(AutoCompleteTextView.class);

        assertThat(((com.google.android.material.textfield.TextInputLayout)view1).getHint()).isEqualTo("How are you feeling?");
        assertThat(((com.google.android.material.textfield.TextInputLayout)view2).getHint()).isEqualTo("What activity have you been doing?");
    }

    @Test
    public void whenQuestionBuilderIsSuppliedWithAllValues_ACompleteQuestionShouldBeReturned() {

        // Check the question text matches
        assertThat(this.firstQuestion.getQuestionText()).isEqualTo("How are you feeling?");

        // Check the type matches
        assertThat(this.firstQuestion.getQuestionType()).isEqualTo(DROP_DOWN_LIST);

        // Check the question list matches
        assertThat(this.firstQuestion.getOptionsList()).isNotNull();
        assertThat(this.firstQuestion.getOptionsList()).isNotEmpty();
        assertThat(this.firstQuestion.getOptionsList()).hasSize(3);

        assertThat(this.secondQuestion.getOptionsList()).hasSize(3);

        assertThat(this.firstQuestion.getOptionsList().get(0)).isEqualTo("Happy");
        assertThat(this.firstQuestion.getOptionsList().get(1)).isEqualTo("Moderate");
        assertThat(this.firstQuestion.getOptionsList().get(2)).isEqualTo("Sad");
    }

    @Test
    public void whenQuestionBuilderIsSuppliedWithoutValues_ThenDefaultValuesShouldBeReturned() {

        SurveyQuestion questionBuilder = new QuestionBuilder().build();
        // Check the question text matches
        assertThat(questionBuilder.getQuestionText()).isEqualTo("");

        // Check the type matches
        assertThat(questionBuilder.getQuestionType()).isEqualTo(null);

        // Check the question list matches
        assertThat(questionBuilder.getOptionsList()).isNotNull();
        assertThat(questionBuilder.getOptionsList()).isEmpty();
    }
}
