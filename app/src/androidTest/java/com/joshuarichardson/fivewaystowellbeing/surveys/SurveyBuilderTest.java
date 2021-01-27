package com.joshuarichardson.fivewaystowellbeing.surveys;

import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.gson.Gson;
import com.joshuarichardson.fivewaystowellbeing.AnswerSurveyActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static com.google.common.truth.Truth.assertThat;
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.DROP_DOWN_LIST;

@HiltAndroidTest
public class SurveyBuilderTest {

    private View surveyBuilder;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule
    public ActivityScenarioRule<AnswerSurveyActivity> answerSurveyActivity = new ActivityScenarioRule<>(AnswerSurveyActivity.class);

    @Before
    public void setUp() {
        Gson gson = new Gson();

        List<QuestionsToAsk> questions = Arrays.asList(
            new QuestionsToAsk("How are you feeling?", "", 1, DROP_DOWN_LIST.name(), 0, gson.toJson(new DropDownListOptionWrapper(Arrays.asList("Facebook", "Snapchat", "Whatsapp")))),
            new QuestionsToAsk("What activity have you been doing?", "", 1, DROP_DOWN_LIST.name(), 0, gson.toJson(new DropDownListOptionWrapper(Arrays.asList("Happy", "Moderate", "Sad"))))
        );

//      https://stackoverflow.com/a/56356650/13496270
        answerSurveyActivity.getScenario().onActivity(
            (activity) -> {
                this.surveyBuilder = new SurveyBuilder(activity)
                .withQuestions(questions)
                .build();
            }
        );
    }

    @After
    public void tearDown() {
        this.surveyBuilder = null;
    }

    @Test
    public void surveyBuilder_ShouldReturnViewWithItem() throws IllegalArgumentException {
        // Check the view exists
        View view = this.surveyBuilder
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
}
