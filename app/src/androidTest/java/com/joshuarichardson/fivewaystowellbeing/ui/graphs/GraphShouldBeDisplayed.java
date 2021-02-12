package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class GraphShouldBeDisplayed {
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

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(Arrays.asList());
            when(surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
                    .thenReturn(data);

            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(questionDao);

            LiveData<List<WellbeingGraphItem>> graphData = new MutableLiveData<>(Arrays.asList());
            when(questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong())).thenReturn(graphData);

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(surveyDao.getLiveInsights(anyString()))
                    .thenReturn(wayToWellbeing);

            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(surveyDao);
            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(questionDao);
            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setup() {
        hiltTest.inject();
    }

    @Test
    public void whenOnFirstPage_ThenAGraphShouldBeDisplayedToUsers() {
        // Check that the graph is displayed
        onView(withId(R.id.graph_card_container)).check(matches(isDisplayed()));
        onView(withId(R.id.graph_card)).check(matches(isDisplayed()));
    }
}
