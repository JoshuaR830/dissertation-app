package com.joshuarichardson.fivewaystowellbeing.ui.surveys;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.RecyclerViewTestUtil.atRecyclerPosition;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class SurveyViewPageShouldBeDisplayedCorrectly {
    @Rule
    public ActivityScenarioRule<MainActivity> answerSurveyActivity = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
            SurveyResponseDao mockSurveyDao = mock(SurveyResponseDao.class);

            SurveyResponse[] responses = new SurveyResponse[] {
                new SurveyResponse(73426786, WaysToWellbeing.CONNECT, "A survey title", "A survey description")
            };

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(mockSurveyDao.getLiveInsights(anyString()))
                    .thenReturn(wayToWellbeing);

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(Arrays.asList(responses));

            when(mockSurveyDao.getAllSurveyResponses()).thenReturn(data);
            when(mockSurveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong())).thenReturn(data);

            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(mockSurveyDao);

            return mockWellbeingDatabase;
        }
    }

    @Test
    public void whenDataInTheDatabase_TextShouldBeDisplayed() {
        onView(withId(R.id.navigation_view_survey_responses)).perform(click());
        onView(withId(R.id.surveyRecyclerView))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(allOf(withId(R.id.survey_list_title), withText("29 Apr 1972"))))))
            .check(matches(atRecyclerPosition(0, hasDescendant(allOf(withId(R.id.survey_list_description), withText("A survey description"))))))
            .check(matches(atRecyclerPosition(0, hasDescendant(allOf(withId(R.id.expand_button), withText("Expand"))))));
    }
}
