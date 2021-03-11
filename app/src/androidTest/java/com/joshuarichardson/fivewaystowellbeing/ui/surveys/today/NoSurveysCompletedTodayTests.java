package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingResultsDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class NoSurveysCompletedTodayTests {

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

            SurveyResponseDao surveyDao = mock(SurveyResponseDao.class);
            WellbeingQuestionDao questionDao = mock(WellbeingQuestionDao.class);
            WellbeingRecordDao wellbeingDao = mock(WellbeingRecordDao.class);
            SurveyResponseActivityRecordDao surveyActivityResponseDao = mock(SurveyResponseActivityRecordDao.class);

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(Arrays.asList());
            when(surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
                .thenReturn(data);

            LiveData<List<WellbeingGraphItem>> graphData = new MutableLiveData<>(Arrays.asList());
            when(questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong())).thenReturn(graphData);

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(surveyDao.getLiveInsights(anyString()))
                    .thenReturn(wayToWellbeing);

            when(surveyDao.getSurveyResponsesByTimestampRangeNotLive(anyLong(), anyLong())).thenReturn(Collections.emptyList());

            WellbeingResultsDao resultsDao = mock(WellbeingResultsDao.class);
            when(mockWellbeingDatabase.wellbeingResultsDao()).thenReturn(resultsDao);

            when(surveyActivityResponseDao.getEmotions(anyLong())).thenReturn(new MutableLiveData<>());

            when(wellbeingDao.getDataBySurvey(anyLong())).thenReturn(new ArrayList<>());

            when(mockWellbeingDatabase.surveyResponseActivityRecordDao()).thenReturn(surveyActivityResponseDao);
            when(mockWellbeingDatabase.wellbeingRecordDao()).thenReturn(wellbeingDao);
            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(questionDao);
            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(surveyDao);
            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setUp() throws InterruptedException {
        hiltTest.inject();
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void WhenNoSurveysCompletedToday_ThenNoSurveysShouldBeDisplayed() {
        onView(withId(R.id.today_survey_item_title))
            .check(doesNotExist());

        onView(withId(R.id.today_survey_item_description))
            .check(doesNotExist());

        onView(withId(R.id.today_survey_item_image_button))
            .check(doesNotExist());
    }

    @Test
    public void WhenNoActivitiesAddedToSurvey_ThenTheCardShouldDisplayAddActivityButton() {
        onView(allOf(withId(R.id.survey_list_title), isDescendantOfA(withId(R.id.survey_summary_item_container))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Today"))));

        onView(allOf(withId(R.id.survey_list_description), isDescendantOfA(withId(R.id.survey_summary_item_container))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Here is how your day is looking so far"))));

        onView(allOf(withId(R.id.pass_time_item), isDescendantOfA(withId(R.id.survey_summary_item_container))))
            .check(doesNotExist());

        onView(allOf(withId(R.id.add_activity_button), isDescendantOfA(withId(R.id.survey_summary_item_container))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Activity"))));
    }
}
