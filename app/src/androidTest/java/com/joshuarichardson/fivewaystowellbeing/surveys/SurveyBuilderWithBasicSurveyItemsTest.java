package com.joshuarichardson.fivewaystowellbeing.surveys;

import android.view.View;
import android.widget.TextView;

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
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.BASIC_SURVEY;

@HiltAndroidTest
public class SurveyBuilderWithBasicSurveyItemsTest {

    private View surveyBuilder;

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule
    public ActivityScenarioRule<AnswerSurveyActivity> answerSurveyActivity = new ActivityScenarioRule<>(AnswerSurveyActivity.class);

    @Before
    public void setUp() {
        String[] apps = new String[]{"Facebook", "Snapchat", "Whatsapp"};

        // How to get the activity https://stackoverflow.com/a/56356650/13496270
        answerSurveyActivity.getScenario().onActivity(
            (activity) -> {
                this.surveyBuilder = new SurveyBuilder(activity)
                    .withBasicSurvey(Arrays.asList(apps))
                    .build();
            }
        );
    }

    @After
    public void tearDown() {
        this.surveyBuilder = null;
    }

    @Test
    public void basicSurvey_ShouldBeIncluded() {
        this.surveyBuilder.requireViewById(R.id.basic_survey_container);
    }

    @Test
    public void basicSurveyContainer_ShouldIncludeCorrectQuestions() {
        View basicContainer = this.surveyBuilder.requireViewById(R.id.basic_survey_container);

        TextView title = basicContainer.requireViewById(R.id.basic_survey_title);
        assertThat(title.getText()).isEqualTo("Survey information");
        basicContainer.requireViewById(R.id.survey_title_input);
        basicContainer.requireViewById(R.id.survey_description_input);
        basicContainer.requireViewById(R.id.survey_activity_input);

        View parent = (View) basicContainer.getParent();
        assertThat(parent.getTag()).isEqualTo(BASIC_SURVEY);
    }
}
