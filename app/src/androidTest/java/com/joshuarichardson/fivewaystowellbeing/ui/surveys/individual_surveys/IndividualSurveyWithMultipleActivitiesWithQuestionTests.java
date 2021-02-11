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
import java.util.Calendar;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.LinearLayoutTestUtil.nthChildOf;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class IndividualSurveyWithMultipleActivitiesWithQuestionTests {

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

            Calendar date = new GregorianCalendar();
            date.set(1999, 2, 29, 15, 10, 0);
            date.getTime();
            // Return a survey response
            SurveyResponse surveyResponse = new SurveyResponse(date.getTime().getTime(), WaysToWellbeing.CONNECT, "Title 1", "Description 1");
            surveyResponse.setSurveyResponseId(123);
            when(surveyResponseDao.getSurveyResponseById(123))
                    .thenReturn(surveyResponse);

            when(wellbeingRecordDao.getDataBySurvey(123))
                .thenReturn(Arrays.asList(
                    new RawSurveyData(date.getTime().getTime(), "Survey description 1", "Activity note 1", "Activity name 1", 1, "Question 1", 1, false, ActivityType.HOBBY.toString()),
                    new RawSurveyData(date.getTime().getTime(), "Survey description 1", "Activity note 1", "Activity name 1", 1, "Question 2", 2, true, ActivityType.HOBBY.toString()),
                    new RawSurveyData(date.getTime().getTime(), "Survey description 1", "Activity note 1", "Activity name 1", 1, "Question 3", 3, true, ActivityType.HOBBY.toString()),
                    new RawSurveyData(date.getTime().getTime(), "Survey description 1", "Activity note 1", "Activity name 1", 1, "Question 4", 4, false, ActivityType.HOBBY.toString()),
                    new RawSurveyData(date.getTime().getTime(), "Survey description 1", "Activity note 2", "Activity name 2", 2, "Question 5", 5, true, ActivityType.HOBBY.toString()),
                    new RawSurveyData(date.getTime().getTime(), "Survey description 1", "Activity note 2", "Activity name 2", 2, "Question 6", 6, false, ActivityType.HOBBY.toString()),
                    new RawSurveyData(date.getTime().getTime(), "Survey description 1", "Activity note 3", "Activity name 3", 3, "Question 7", 7, false, ActivityType.HOBBY.toString()),
                    new RawSurveyData(date.getTime().getTime(), "Survey description 1", "Activity note 3", "Activity name 3", 3, "Question 8", 8, true, ActivityType.HOBBY.toString())
                )
            );

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
    }

    @Test
    public void whenOnIndividualSurveyPageAndMultipleActivities_AllQuestionsShouldBeDisplayed() {
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
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .perform(click());

        onView(allOf(withId(R.id.check_box_container), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0)), nthChildOf(withId(R.id.check_box_container), 0)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 1")))
            .check(matches(isNotChecked()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0)), nthChildOf(withId(R.id.check_box_container), 1)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 2")))
            .check(matches(isChecked()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0)), nthChildOf(withId(R.id.check_box_container), 2)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 3")))
            .check(matches(isChecked()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0)), nthChildOf(withId(R.id.check_box_container), 3)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 4")))
            .check(matches(isNotChecked()));

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Activity name 2")));

        onView(allOf(withId(R.id.activity_note_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Activity note 2")));

        onView(allOf(withId(R.id.expand_options_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .check(matches(isDisplayed()))
            .perform(scrollTo())
            .perform(click());

        onView(allOf(withId(R.id.check_box_container), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1)), nthChildOf(withId(R.id.check_box_container), 0)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 5")))
            .check(matches(isChecked()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1)), nthChildOf(withId(R.id.check_box_container), 1)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 6")))
            .check(matches(isNotChecked()));

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 2))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Activity name 3")));

        onView(allOf(withId(R.id.activity_note_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 2))))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Activity note 3")));

        onView(allOf(withId(R.id.expand_options_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 2))))
                .check(matches(isDisplayed()))
                .perform(scrollTo())
                .perform(click());

        onView(allOf(withId(R.id.check_box_container), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 2))))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 2)), nthChildOf(withId(R.id.check_box_container), 0)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 7")))
            .check(matches(isNotChecked()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 2)), nthChildOf(withId(R.id.check_box_container), 1)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 8")))
            .check(matches(isChecked()));
    }
}
