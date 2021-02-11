package com.joshuarichardson.fivewaystowellbeing.ui.surveys.individual_surveys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.IndividualSurveyActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.GregorianCalendar;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.LinearLayoutTestUtil.nthChildOf;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class IndividualSurveyWithOneActivityWithQuestionsTests {

    // Reference https://stackoverflow.com/a/57777912/13496270
    static Intent surveyIntent;
    static {
        surveyIntent = new Intent(ApplicationProvider.getApplicationContext(), IndividualSurveyActivity.class);
        Bundle surveyBundle = new Bundle();
        surveyBundle.putLong("survey_id", 123);
        surveyIntent.putExtras(surveyBundle);
    }

    @Rule(order = 0)
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule(order = 1)
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule(order = 2)
    public ActivityScenarioRule<IndividualSurveyActivity> surveyActivityRule = new ActivityScenarioRule<>(surveyIntent);

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
            SurveyResponseDao surveyResponseDao = mock(SurveyResponseDao.class);
            WellbeingRecordDao wellbeingRecordDao = mock(WellbeingRecordDao.class);

            long time = new GregorianCalendar(1999, 2, 29, 15, 10, 0).getTimeInMillis();

            // Return a survey response
            SurveyResponse surveyResponse = new SurveyResponse(time, WaysToWellbeing.CONNECT, "Title 1", "Description 1");
            surveyResponse.setSurveyResponseId(123);
            when(surveyResponseDao.getSurveyResponseById(123))
                    .thenReturn(surveyResponse);

            when(wellbeingRecordDao.getDataBySurvey(123))
                .thenReturn(Arrays.asList(
                    new RawSurveyData(time, "Survey description 1", "Activity note 1", "Activity name 1", 1, "Question 1", 1, false, ActivityType.HOBBY.toString()),
                    new RawSurveyData(time, "Survey description 1", "Activity note 1", "Activity name 1", 1, "Question 2", 1, true, ActivityType.HOBBY.toString())
                )
            );

            // Ensure that the database returns the DAOs
            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(surveyResponseDao);
            when(mockWellbeingDatabase.wellbeingRecordDao()).thenReturn(wellbeingRecordDao);

            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setup() {
        hiltTest.inject();
    }

    @Test
    public void whenOnIndividualSurveyPage_ASummaryShouldBeDisplayed() {
        onView(withId(R.id.survey_summary))
            .check(matches(isDisplayed()));

        onView(withId(R.id.survey_summary_title))
            .check(matches(allOf(isDisplayed(), withText("Summary"))));

        onView(withId(R.id.individual_survey_title))
            .check(matches(allOf(isDisplayed(), withText("Title 1"))));

        onView(withId(R.id.individual_survey_description))
            .check(matches(allOf(isDisplayed(), withText("Description 1"))));

        onView(withId(R.id.individual_survey_time))
            .check(matches(allOf(isDisplayed(), withText("29 Mar 1999 15:10"))));

        onView(withId(R.id.graph_card_container)).check(matches(isDisplayed()));
        onView(withId(R.id.graph_card)).check(matches(isDisplayed()));
    }

    @Test
    public void whenOnIndividualSurveyPageAndASingleActivity_AllQuestionsShouldBeDisplayed() {
        onView(allOf(withId(R.id.survey_list_title), isDescendantOfA(withId(R.id.survey_summary_item_container))))
            .perform(scrollTo())
            .check(matches(withText("29 Mar 1999")));

        onView(allOf(withId(R.id.survey_list_description), isDescendantOfA(withId(R.id.survey_summary_item_container))))
            .perform(scrollTo())
            .check(matches(withText("Survey description 1")));

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Activity name 1")));

        onView(allOf(withId(R.id.activity_note_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Activity note 1")));

        onView(allOf(withId(R.id.expand_options_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .check(matches(isDisplayed()))
            .perform(click());

        onView(allOf(withId(R.id.check_box_container), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0)), nthChildOf(withId(R.id.check_box_container), 0)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 1")));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0)), nthChildOf(withId(R.id.check_box_container), 1)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 2")));

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .check(doesNotExist());
    }
}
