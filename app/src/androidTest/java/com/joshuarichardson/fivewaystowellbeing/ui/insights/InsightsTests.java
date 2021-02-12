package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

            SurveyResponseDao surveyDao = mock(SurveyResponseDao.class);
            WellbeingQuestionDao questionDao = mock(WellbeingQuestionDao.class);

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(new ArrayList<>());
            when(surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
                    .thenReturn(data);

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(surveyDao.getLiveInsights(anyString()))
                    .thenReturn(wayToWellbeing);


            LiveData<List<WellbeingGraphItem>> graphData = new MutableLiveData<>(Arrays.asList());
            when(questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong())).thenReturn(graphData);
            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(questionDao);

            when(surveyDao.getInsights("CONNECT")).thenReturn(1);
            when(surveyDao.getInsights("BE_ACTIVE")).thenReturn(64);
            when(surveyDao.getInsights("TAKE_NOTICE")).thenReturn(99);
            when(surveyDao.getInsights("KEEP_LEARNING")).thenReturn(56);
            when(surveyDao.getInsights("GIVE")).thenReturn(7);

            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(surveyDao);
            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setup() {
        hiltTest.inject();
    }

    @Test
    public void whenNavigatingToInsights_TheRecyclerViewShouldContain6Items() {
       onView(withId(R.id.navigation_insights)).perform(click());

       onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(withText("View your activities")))));

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(1))
            .check(matches(atRecyclerPosition(1, hasDescendant(withText("Times achieved: Connect")))))
            .check(matches(atRecyclerPosition(1, hasDescendant(withText("1")))));

        onView(withId(R.id.insights_recycler_view))
                .perform(scrollToPosition(2))
                .check(matches(atRecyclerPosition(2, hasDescendant(withText("Times achieved: Be active")))))
                .check(matches(atRecyclerPosition(2, hasDescendant(withText("64")))));

        onView(withId(R.id.insights_recycler_view))
                .perform(scrollToPosition(3))
                .check(matches(atRecyclerPosition(3, hasDescendant(withText("Times achieved: Keep learning")))))
                .check(matches(atRecyclerPosition(3, hasDescendant(withText("56")))));

        onView(withId(R.id.insights_recycler_view))
                .perform(scrollToPosition(4))
                .check(matches(atRecyclerPosition(4, hasDescendant(withText("Times achieved: Take notice")))))
                .check(matches(atRecyclerPosition(4, hasDescendant(withText("99")))));

        onView(withId(R.id.insights_recycler_view))
                .perform(scrollToPosition(5))
                .check(matches(atRecyclerPosition(5, hasDescendant(withText("Times achieved: Give")))))
                .check(matches(atRecyclerPosition(5, hasDescendant(withText("7")))));
    }
}
