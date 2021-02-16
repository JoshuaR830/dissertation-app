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
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class AddingActivityToSurveyWhenNoSurveyExistsTests {
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

            AddingActivityToSurveyWhenNoSurveyExistsTests.this.surveyDao = mock(SurveyResponseDao.class);
            AddingActivityToSurveyWhenNoSurveyExistsTests.this.questionDao = mock(WellbeingQuestionDao.class);
            AddingActivityToSurveyWhenNoSurveyExistsTests.this.wellbeingDao = mock(WellbeingRecordDao.class);
            AddingActivityToSurveyWhenNoSurveyExistsTests.this.surveyResponseActivityDao = mock(SurveyResponseActivityRecordDao.class);
            ActivityRecordDao activitiesDao = mock(ActivityRecordDao.class);

            LiveData<List<ActivityRecord>> activityData = new MutableLiveData<>(
                Arrays.asList(
                    new ActivityRecord("Activity 1", 2000, 123456, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE),
                    new ActivityRecord("Activity 2", 3000, 437724, ActivityType.HOBBY, WaysToWellbeing.KEEP_LEARNING)
                )
            );

            when(activitiesDao.getAllActivities()).thenReturn(activityData);

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(new ArrayList<>());
            when(AddingActivityToSurveyWhenNoSurveyExistsTests.this.surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
                .thenReturn(data);

            LiveData<List<WellbeingGraphItem>> graphData = new MutableLiveData<>(Arrays.asList());
            when(AddingActivityToSurveyWhenNoSurveyExistsTests.this.questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong()))
                .thenReturn(graphData);

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(AddingActivityToSurveyWhenNoSurveyExistsTests.this.surveyDao.getLiveInsights(anyString()))
                    .thenReturn(wayToWellbeing);

            when(AddingActivityToSurveyWhenNoSurveyExistsTests.this.wellbeingDao.getDataBySurvey(anyLong())).thenReturn(new ArrayList<>());

            when(AddingActivityToSurveyWhenNoSurveyExistsTests.this.surveyResponseActivityDao.insert(any(SurveyResponseActivityRecord.class)))
                .thenReturn(1L);

            when(mockWellbeingDatabase.wellbeingRecordDao()).thenReturn(AddingActivityToSurveyWhenNoSurveyExistsTests.this.wellbeingDao);
            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(AddingActivityToSurveyWhenNoSurveyExistsTests.this.questionDao);
            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(AddingActivityToSurveyWhenNoSurveyExistsTests.this.surveyDao);
            when(mockWellbeingDatabase.surveyResponseActivityRecordDao()).thenReturn(AddingActivityToSurveyWhenNoSurveyExistsTests.this.surveyResponseActivityDao);
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
    public void whenASurveyExists_ShouldNotInsertANewOneThenShouldBeAbleToAddAnActivityToIt() throws InterruptedException {
        // When no survey exists there should be an insertion
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        verify(this.surveyDao, times(1)).getSurveyResponsesByTimestampRange(anyLong(), anyLong());
        verify(this.surveyDao, times(1)).insert(any(SurveyResponse.class));

        // Start on main activity - this should launch the passtime view
        onView(withId(R.id.add_activity_button))
            .perform(scrollTo(), click());

        // This should select an item from the activity
        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(0))
            .check(matches(isDisplayed()))
            .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }
}
