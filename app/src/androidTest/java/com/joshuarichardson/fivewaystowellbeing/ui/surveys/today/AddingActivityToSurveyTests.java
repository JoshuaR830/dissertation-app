package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.contrib.RecyclerViewActions;
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
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class AddingActivityToSurveyTests {
    private SurveyResponseDao surveyDao;
    private WellbeingQuestionDao questionDao;
    private WellbeingRecordDao wellbeingDao;
    private SurveyResponseActivityRecordDao surveyResponseActivityDao;

    @Rule(order = 0)
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule(order = 1)
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule(order = 2)
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {

        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);

            AddingActivityToSurveyTests.this.surveyDao = mock(SurveyResponseDao.class);
            AddingActivityToSurveyTests.this.questionDao = mock(WellbeingQuestionDao.class);
            AddingActivityToSurveyTests.this.wellbeingDao = mock(WellbeingRecordDao.class);
            AddingActivityToSurveyTests.this.surveyResponseActivityDao = mock(SurveyResponseActivityRecordDao.class);

            ActivityRecordDao activitiesDao = mock(ActivityRecordDao.class);

            LiveData<List<ActivityRecord>> activityData = new MutableLiveData<>(
                Arrays.asList(
                    new ActivityRecord("Activity 1", 2000, 123456, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE, false),
                    new ActivityRecord("Activity 2", 3000, 437724, ActivityType.HOBBY, WaysToWellbeing.KEEP_LEARNING, false),
                    new ActivityRecord("Activity 3", 3000, 437724, ActivityType.LEARNING, WaysToWellbeing.KEEP_LEARNING, false)
                )
            );

            when(activitiesDao.getAllActivities()).thenReturn(activityData);

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(Collections.singletonList(new SurveyResponse(12345, WaysToWellbeing.UNASSIGNED, "Title", "Note", 0, 0, 0, 0, 0)));
            when(AddingActivityToSurveyTests.this.surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
                .thenReturn(data);

            LiveData<List<WellbeingGraphItem>> graphData = new MutableLiveData<>(new ArrayList<>());
            when(AddingActivityToSurveyTests.this.questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong()))
                .thenReturn(graphData);

            when(AddingActivityToSurveyTests.this.questionDao.getQuestionsByActivityType(ActivityType.SPORT.toString())).thenReturn(
                Arrays.asList(
                    new WellbeingQuestion(1, "Question 1", "Positive message 1", "Negative message 1", WaysToWellbeing.BE_ACTIVE.toString(), 1, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()),
                    new WellbeingQuestion(2, "Question 2", "Positive message 2", "Negative message 2", WaysToWellbeing.BE_ACTIVE.toString(), 4, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()),
                    new WellbeingQuestion(3, "Question 3", "Positive message 3", "Negative message 3", WaysToWellbeing.BE_ACTIVE.toString(), 3, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString())
                )
            );

            when(AddingActivityToSurveyTests.this.questionDao.getQuestionsByActivityType(ActivityType.HOBBY.toString())).thenReturn(new ArrayList<>());

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(AddingActivityToSurveyTests.this.surveyDao.getLiveInsights(anyString()))
                    .thenReturn(wayToWellbeing);

            when(AddingActivityToSurveyTests.this.wellbeingDao.getDataBySurvey(anyLong())).thenReturn(new ArrayList<>());

            when(AddingActivityToSurveyTests.this.surveyResponseActivityDao.getEmotions(anyLong())).thenReturn(new MutableLiveData<>());

            when(mockWellbeingDatabase.wellbeingRecordDao()).thenReturn(AddingActivityToSurveyTests.this.wellbeingDao);
            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(AddingActivityToSurveyTests.this.questionDao);
            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(AddingActivityToSurveyTests.this.surveyDao);
            when(mockWellbeingDatabase.surveyResponseActivityRecordDao()).thenReturn(AddingActivityToSurveyTests.this.surveyResponseActivityDao);
            when(mockWellbeingDatabase.activityRecordDao()).thenReturn(activitiesDao);
            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setUp() throws InterruptedException {
        hiltTest.inject();
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenASurveyExists_ShouldNotInsertANewOneThenShouldBeAbleToAddAnActivityToIt() {

        // When a survey exists there should not be an insertion
        verify(this.surveyDao, Mockito.atLeast(1)).getSurveyResponsesByTimestampRange(anyLong(), anyLong());

        // Start on main activity - this should launch the passtime view
        onView(withId(R.id.add_activity_button))
            .perform(scrollTo(), click());

        // This should select an item from the activity
        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(0))
            .check(matches(isDisplayed()))
            .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(withText("Activity 1")));

        onView(allOf(withId(R.id.pass_time_item), nthChildOf(withId(R.id.survey_item_container), 0)))
            .perform(scrollTo())
            .check(matches(withTagValue(is((Object) "BE_ACTIVE"))));

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
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Question 3")));

        onView(withId(R.id.add_activity_button))
            .perform(scrollTo(), click());

        // This should select an item from the activity
        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(1))
            .check(matches(isDisplayed()))
            .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .perform(scrollTo())
            .check(matches(withText("Activity 2")));

        onView(allOf(withId(R.id.pass_time_item), nthChildOf(withId(R.id.survey_item_container), 1)))
            .perform(scrollTo())
            .check(matches(withTagValue(is((Object) "KEEP_LEARNING"))));

        onView(allOf(withId(R.id.expand_options_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .check(matches(not(isDisplayed())));
    }
}
