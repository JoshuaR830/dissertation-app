package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
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
import java.util.Date;
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
public class SurveysCompletedTodayTests {

    @Rule(order = 0)
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule(order = 1)
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule(order = 2)
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {

        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
            WellbeingQuestionDao questionDao = mock(WellbeingQuestionDao.class);

            Date now = new Date();

            List<SurveyResponse> list = Arrays.asList(
                new SurveyResponse(now.getTime(), WaysToWellbeing.CONNECT.name(), "title 1", "description 1"),
                new SurveyResponse(now.getTime(), WaysToWellbeing.BE_ACTIVE.name(), "title 2", "description 2"));

            LiveData<List<WellbeingGraphItem>> graphData = new MutableLiveData<>(Arrays.asList());
            when(questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong())).thenReturn(graphData);

            SurveyResponseDao surveyDao = mock(SurveyResponseDao.class);

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(surveyDao.getLiveInsights(anyString()))
                    .thenReturn(wayToWellbeing);

            when(surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
                .thenReturn(new MutableLiveData<>(list));
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
    public void WhenThereAreSurveys_ThenTheNoSurveyTitleShouldNotBeDisplayed() {
        onView(withId(R.id.no_surveys_title)).check(doesNotExist());
        onView(withId(R.id.no_surveys_description)).check(doesNotExist());
        onView(withId(R.id.no_surveys_button)).check(doesNotExist());
    }

    @Test
    public void WhenThereAre2Surveys_ThenBothSurveysShouldBeDisplayed() {
        onView(allOf(withId(R.id.today_survey_item_title), isDescendantOfA(withId(0))))
                .perform(scrollTo())
                .check(matches(allOf(isDisplayed(), withText("title 1"))));
        onView(allOf(withId(R.id.today_survey_item_description), isDescendantOfA(withId(0))))
                .perform(scrollTo())
                .check(matches(allOf(isDisplayed(), withText("description 1"))));
        onView(allOf(withId(R.id.today_survey_item_image_button), isDescendantOfA(withId(0))))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.today_survey_item_title), isDescendantOfA(withId(1))))
                .perform(scrollTo())
                .check(matches(allOf(isDisplayed(), withText("title 2"))));
        onView(allOf(withId(R.id.today_survey_item_description), isDescendantOfA(withId(1))))
                .perform(scrollTo())
                .check(matches(allOf(isDisplayed(), withText("description 2"))));
        onView(allOf(withId(R.id.today_survey_item_image_button), isDescendantOfA(withId(1))))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }
}
