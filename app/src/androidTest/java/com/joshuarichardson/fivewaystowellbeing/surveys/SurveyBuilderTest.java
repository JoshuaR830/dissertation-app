package com.joshuarichardson.fivewaystowellbeing.surveys;

import android.content.Context;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.joshuarichardson.fivewaystowellbeing.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.DROP_DOWN_LIST;

public class SurveyBuilderTest {

    private View surveyBuilder;
    private SurveyQuestion question;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();

        String[] feelings = new String[]{"Happy", "Moderate", "Sad"};

        this.question = new QuestionBuilder()
                .withQuestionText("How are you feeling?")
                .withOptionsList(Arrays.asList(feelings))
                .withType(DROP_DOWN_LIST)
                .build();

        this.surveyBuilder = new SurveyBuilder(context)
                .withQuestion(this.question)
                .build();
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
            .requireViewById(R.layout.activity_answer_survey)
            .requireViewById(0)
            .requireViewById(R.id.drop_down_input);

        // Check that the type is an auto complete text type as that is used for drop down lists
        assertThat(view).isInstanceOf(AutoCompleteTextView.class);
    }

    @Test
    public void whenQuestionBuilderIsSuppliedWithAllValues_ACompleteQuestionShouldBeReturned() {

        // Check the question text matches
        assertThat(this.question.getQuestionText()).isEqualTo("How are you feeling?");

        // Check the type matches
        assertThat(this.question.getType()).isEqualTo(DROP_DOWN_LIST);

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
        assertThat(this.question.getType()).isEqualTo(null);

        // Check the question list matches
        assertThat(this.question.getOptionsList()).isNotNull();
        assertThat(this.question.getOptionsList()).isEmpty();
    }
}
