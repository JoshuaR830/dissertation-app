package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.ActivityStats;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingResultsDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.RecyclerViewTestUtil.atRecyclerPosition;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class InsightsTests {
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
            WellbeingRecordDao wellbeingDao = mock(WellbeingRecordDao.class);

            SurveyResponseActivityRecordDao surveyActivityDao = mock(SurveyResponseActivityRecordDao.class);
            ActivityRecordDao activityDao = mock(ActivityRecordDao.class);

            // Return the activities
            when(activityDao.getActivityRecordById(1)).thenReturn(new ActivityRecord("Talk", 453876587, 784687, "PEOPLE", "CONNECT", false));
            when(activityDao.getActivityRecordById(2)).thenReturn(new ActivityRecord("Place holder", 453876587, 784687, "SPORT", "BE_ACTIVE", false));
            when(activityDao.getActivityRecordById(3)).thenReturn(new ActivityRecord("Washing up", 453876587, 784687, "CHORES", "GIVE", false));

            SurveyResponseDao surveyDao = mock(SurveyResponseDao.class);
            WellbeingQuestionDao questionDao = mock(WellbeingQuestionDao.class);

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(new ArrayList<>());
            when(surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
                    .thenReturn(data);

            when(surveyDao.getSurveyResponsesByTimestampRangeNotLive(anyLong(), anyLong())).thenReturn(Collections.emptyList());

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(surveyDao.getLiveInsights(anyString()))
                    .thenReturn(wayToWellbeing);

            LiveData<List<WellbeingGraphItem>> graphData = new MutableLiveData<>(Arrays.asList());
            when(questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong())).thenReturn(graphData);
            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(questionDao);

            when(questionDao.getWaysToWellbeingBetweenTimesNotLive(anyLong(), anyLong())).thenReturn(Arrays.asList(new WellbeingGraphItem(WaysToWellbeing.CONNECT.toString(), 140), new WellbeingGraphItem(WaysToWellbeing.BE_ACTIVE.toString(), 90), new WellbeingGraphItem(WaysToWellbeing.KEEP_LEARNING.toString(), 120), new WellbeingGraphItem(WaysToWellbeing.TAKE_NOTICE.toString(), 100), new WellbeingGraphItem(WaysToWellbeing.GIVE.toString(), 20)));

            when(wellbeingDao.getDataBySurvey(anyLong())).thenReturn(Collections.singletonList(new RawSurveyData(357457, "Survey note", "Activity note", "Activity name", 1, "Question", 1, true, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString(), -1, -1, 0, false)));

            // Return activities by frequency
            when(surveyActivityDao.getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("CONNECT"))).thenReturn(Arrays.asList(new ActivityStats(1, 3), new ActivityStats(2, 2), new ActivityStats(2, 1)));
            when(surveyActivityDao.getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("BE_ACTIVE"))).thenReturn(Arrays.asList(new ActivityStats(2, 3), new ActivityStats(2, 2), new ActivityStats(2, 1)));
            when(surveyActivityDao.getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("KEEP_LEARNING"))).thenReturn(Arrays.asList(new ActivityStats(2, 9), new ActivityStats(2, 4), new ActivityStats(2, 1)));
            when(surveyActivityDao.getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("TAKE_NOTICE"))).thenReturn(Arrays.asList(new ActivityStats(2, 7), new ActivityStats(2, 5), new ActivityStats(2, 2)));
            when(surveyActivityDao.getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("GIVE"))).thenReturn(Arrays.asList(new ActivityStats(2, 7), new ActivityStats(2, 4), new ActivityStats(3, 1)));

            WellbeingResultsDao resultsDao = mock(WellbeingResultsDao.class);
            when(resultsDao.getResultsByTimestampRange(anyLong(), anyLong())).thenReturn(Arrays.asList(
                new WellbeingResult(1, 12345, 100, 100, 100, 10, 20),
                new WellbeingResult(2, 23456, 100, 100, 30, 100, 20),
                new WellbeingResult(3, 34567, 100, 20, 30, 10, 20)
            ));

            when(mockWellbeingDatabase.wellbeingResultsDao()).thenReturn(resultsDao);
            when(mockWellbeingDatabase.surveyResponseActivityRecordDao()).thenReturn(surveyActivityDao);
            when(mockWellbeingDatabase.activityRecordDao()).thenReturn(activityDao);
            when(mockWellbeingDatabase.wellbeingRecordDao()).thenReturn(wellbeingDao);
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
    public void whenNavigatingToInsights_TheRecyclerViewShouldContain9Items() {
       onView(withId(R.id.navigation_insights)).perform(click());

       onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(withId(R.id.time_chip)))));

       onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(1))
            .check(matches(atRecyclerPosition(1, hasDescendant(withText("Daily ways to wellbeing")))))
            .check(matches(atRecyclerPosition(1, hasDescendant(withId(R.id.daily_wellbeing_line_chart)))))
            .check(matches(atRecyclerPosition(1, hasDescendant(withId(R.id.wellbeing_line_graph_key)))))
            .check(matches(atRecyclerPosition(1, hasDescendant(withId(R.id.wellbeing_info_button)))));

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(2))
            .check(matches(atRecyclerPosition(2, hasDescendant(withText("You are doing best at connect")))))
            .check(matches(atRecyclerPosition(2, hasDescendant(withText("Favourite activity: Talk")))))
            .check(matches(atRecyclerPosition(2, hasDescendant(withText("You did this regularly over the last week - keep it up!")))));

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(3))
            .check(matches(atRecyclerPosition(3, hasDescendant(withText("You could work on your give score")))))
            .check(matches(atRecyclerPosition(3, hasDescendant(withText("Suggested activity: Washing up")))))
            .check(matches(atRecyclerPosition(3, hasDescendant(withText("Doing this more regularly will help to boost your daily give score")))));

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(4))
            .check(matches(atRecyclerPosition(4, hasDescendant(withText("Times achieved:")))))
            .check(matches(atRecyclerPosition(4, hasDescendant(withText("Connect")))))
            .check(matches(atRecyclerPosition(4, hasDescendant(withText("3")))));

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(5))
            .check(matches(atRecyclerPosition(5, hasDescendant(withText("Times achieved:")))))
            .check(matches(atRecyclerPosition(5, hasDescendant(withText("Be active")))))
            .check(matches(atRecyclerPosition(5, hasDescendant(withText("2")))));

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(6))
            .check(matches(atRecyclerPosition(6, hasDescendant(withText("Times achieved:")))))
            .check(matches(atRecyclerPosition(6, hasDescendant(withText("Keep learning")))))
            .check(matches(atRecyclerPosition(6, hasDescendant(withText("1")))));

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(7))
            .check(matches(atRecyclerPosition(7, hasDescendant(withText("Times achieved:")))))
            .check(matches(atRecyclerPosition(7, hasDescendant(withText("Take notice")))))
            .check(matches(atRecyclerPosition(7, hasDescendant(withText("1")))));

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(8))
            .check(matches(atRecyclerPosition(8, hasDescendant(withText("Times achieved:")))))
            .check(matches(atRecyclerPosition(8, hasDescendant(withText("Give")))))
            .check(matches(atRecyclerPosition(8, hasDescendant(withText("0")))));
    }
}
