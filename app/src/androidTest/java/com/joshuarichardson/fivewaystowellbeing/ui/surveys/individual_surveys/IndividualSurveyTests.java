package com.joshuarichardson.fivewaystowellbeing.ui.surveys.individual_surveys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.IndividualSurveyActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.RecyclerViewTestUtil.atRecyclerPosition;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class IndividualSurveyTests {

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
            SurveyResponseElementDao surveyResponseElementDao = mock(SurveyResponseElementDao.class);
            SurveyResponseActivityRecordDao surveyActivityResponseDao = mock(SurveyResponseActivityRecordDao.class);

            when(surveyActivityResponseDao.getActivitiesBySurveyId(123)).thenReturn(Arrays.asList(new ActivityRecord("Activity", 2000, 23648726, ActivityType.SPORT, WaysToWellbeing.CONNECT)));

            Calendar date = new GregorianCalendar();
            date.set(1999, 2, 29, 15, 10, 0);
            date.getTime();
            // Return a survey response
            SurveyResponse surveyResponse = new SurveyResponse(date.getTime().getTime(), WaysToWellbeing.CONNECT, "Title 1", "Description 1");
            surveyResponse.setSurveyResponseId(123);
            when(surveyResponseDao.getSurveyResponseById(123))
                    .thenReturn(surveyResponse);

            // Return survey elements
            LiveData<List<SurveyResponseElement>> surveyResponseElementList = new MutableLiveData<>(Arrays.asList(
                new SurveyResponseElement(123, "Question 1", "Answer 1"),
                new SurveyResponseElement(123, "Question 2", "Answer 2"),
                new SurveyResponseElement(123, "Question 3", "Answer 3")
            ));
            when(surveyResponseElementDao.getSurveyResponseElementBySurveyResponseId(123))
                .thenReturn(surveyResponseElementList);

            // Ensure that the database returns the DAOs
            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(surveyResponseDao);
            when(mockWellbeingDatabase.surveyResponseActivityRecordDao()).thenReturn(surveyActivityResponseDao);
            when(mockWellbeingDatabase.surveyResponseElementDao()).thenReturn(surveyResponseElementDao);

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
    }

    @Test
    public void whenOnIndividualSurveyPage_AnActivityShouldBeDisplayed() {
        // ToDo if surveys get multiple activities in future iterations - update to have multiple
        // check that the activity is displayed
        onView(allOf( withId(R.id.nameTextView), isDescendantOfA(withId(R.id.individual_survey_activity))))
            .check(matches(withText("Activity")));
    }

    @Test
    public void whenOnIndividualSurveyPage_AllQuestionsShouldBeDisplayed() {
        onView(withId(R.id.survey_summary_recycler_view))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(allOf(withId(R.id.survey_question), withText("Question 1"))))))
            .check(matches(atRecyclerPosition(0, hasDescendant(allOf(withId(R.id.survey_answer), withText("Answer 1"))))))
            .perform(scrollToPosition(1))
            .check(matches(atRecyclerPosition(1, hasDescendant(allOf(withId(R.id.survey_question), withText("Question 2"))))))
            .check(matches(atRecyclerPosition(1, hasDescendant(allOf(withId(R.id.survey_answer), withText("Answer 2"))))))
            .perform(scrollToPosition(2))
            .check(matches(atRecyclerPosition(2, hasDescendant(allOf(withId(R.id.survey_question), withText("Question 3"))))))
            .check(matches(atRecyclerPosition(2, hasDescendant(allOf(withId(R.id.survey_answer), withText("Answer 3"))))));
    }
}
