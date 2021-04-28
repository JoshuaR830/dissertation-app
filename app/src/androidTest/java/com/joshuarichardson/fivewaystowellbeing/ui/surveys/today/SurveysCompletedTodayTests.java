package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.ProgressFragmentTestFixture;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
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
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.LinearLayoutTestUtil.nthChildOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class SurveysCompletedTodayTests extends ProgressFragmentTestFixture {

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {

        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            // Set up the default items to return
            defaultResponses();

            // Return the DAOs from the DB
            mockDatabaseResponses();

            return mockWellbeingDatabase;
        }
    }

    @Override
    protected void defaultResponses() {
        super.defaultResponses();

        long now = Calendar.getInstance().getTimeInMillis();

        List<SurveyResponse> list = Arrays.asList(
            new SurveyResponse(now, WaysToWellbeing.CONNECT.name(), "title 1", "description 1"),
            new SurveyResponse(now, WaysToWellbeing.BE_ACTIVE.name(), "title 2", "description 2"));

        when(wellbeingDao.getDataBySurvey(anyLong())).thenReturn(Arrays.asList(
            new RawSurveyData(now, "Survey note", "Activity note 1", "Activity name 1", 1, "Question 1", 1, false, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString(), -1, -1, 0, false),
            new RawSurveyData(now, "Survey note", "Activity note 1", "Activity name 1", 1, "Question 2", 2, false, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString(), -1, -1, 0, false),
            new RawSurveyData(now, "Survey note", "Activity note 2", "Activity name 2", 2, "Question 1", 3, false, ActivityType.LEARNING.toString(), WaysToWellbeing.KEEP_LEARNING.toString(), -1, -1, 0, false),
            new RawSurveyData(now, "Survey note", "", "Activity name 3", 3, "Question 1", 4, false, ActivityType.LEARNING.toString(), WaysToWellbeing.KEEP_LEARNING.toString(), -1, -1, 0, false)
        ));

        when(surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
            .thenReturn(new MutableLiveData<>(list));
    }

    @Test
    public void OldTodayView_ShouldNotBeDisplayed() {
        onView(allOf(withId(R.id.today_survey_item_title), isDescendantOfA(withId(0))))
            .check(doesNotExist());
    }

    @Test
    public void WhenMultipleActivitiesAreAddedToASurvey_ThenAllActivitiesShouldBeDisplayed() {
        onView(allOf(withId(R.id.survey_list_title), isDescendantOfA(withId(R.id.survey_summary_item_container))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Today"))));

        onView(allOf(withId(R.id.survey_list_description), isDescendantOfA(withId(R.id.survey_summary_item_container))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Survey note"))));

        onView(allOf(withId(R.id.activity_item), nthChildOf(withId(R.id.survey_item_container), 0)))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Activity name 1"))));

        onView(allOf(withId(R.id.activity_item), nthChildOf(withId(R.id.survey_item_container), 0)))
            .perform(scrollTo())
            .check(matches(withTagValue(is((Object) "KEEP_LEARNING"))));

        onView(allOf(withId(R.id.activity_note_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Activity note 1"))));

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

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0)), nthChildOf(withId(R.id.check_box_container), 2)))
            .check(doesNotExist());

        onView(allOf(withId(R.id.done_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo(), click());

        onView(allOf(withId(R.id.check_box_container), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.activity_item), nthChildOf(withId(R.id.survey_item_container), 1)))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Activity name 2"))));

        onView(allOf(withId(R.id.activity_item), nthChildOf(withId(R.id.survey_item_container), 1)))
            .perform(scrollTo())
            .check(matches(withTagValue(is((Object) "KEEP_LEARNING"))));

        onView(allOf(withId(R.id.activity_note_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Activity note 2"))));

        onView(allOf(withId(R.id.add_activity_button), isDescendantOfA(withId(R.id.survey_summary_item_container))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Activity"))));

        onView(allOf(withId(R.id.check_box_container), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1)), nthChildOf(withId(R.id.check_box_container), 0)))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 1")));

        onView(allOf(withId(R.id.checkbox), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1)), nthChildOf(withId(R.id.check_box_container), 1)))
            .check(doesNotExist());

        onView(allOf(withId(R.id.activity_item), nthChildOf(withId(R.id.survey_item_container), 2)))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.activity_item), nthChildOf(withId(R.id.survey_item_container), 2)))
            .perform(scrollTo())
            .check(matches(withTagValue(is((Object) "KEEP_LEARNING"))));

        onView(allOf(withId(R.id.activity_note_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 2))))
            .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.activity_item), nthChildOf(withId(R.id.survey_item_container), 3)))
            .check(doesNotExist());
    }
}
