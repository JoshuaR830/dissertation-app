package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
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
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class NoSurveysCompletedTodayTests {
    private SurveyResponseDao surveyDao;

    @Rule(order = 0)
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule(order = 1)
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule(order = 2)
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);


    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {

        // ToDo - make it so that surveys by time returns only things from today
        // ToDo - how can I check that only items from correct time range are done
        // ToDo - create a function that checks that the time returned is

        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(new ArrayList<>());

            NoSurveysCompletedTodayTests.this.surveyDao = mock(SurveyResponseDao.class);
            when(NoSurveysCompletedTodayTests.this.surveyDao.getSurveyResponsesByTimestampRange(anyInt(), anyInt()))
                .thenReturn(data);
            NoSurveysCompletedTodayTests.this.surveyDao = mock(SurveyResponseDao.class);
            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(NoSurveysCompletedTodayTests.this.surveyDao);

            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setup() {
        hiltTest.inject();
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
    public void WhenNoSurveysCompletedToday_ThenTheCardShouldDisplayNoSurveysYet() {
        onView(withId(R.id.no_surveys_title)).check(matches(isDisplayed()));
        onView(withId(R.id.no_surveys_description)).check(matches(isDisplayed()));
        onView(withId(R.id.no_surveys_button)).check(matches(isDisplayed()));
    }
}
