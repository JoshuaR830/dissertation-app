package com.joshuarichardson.fivewaystowellbeing.ui.surveys;

import android.content.Context;
import android.widget.ImageView;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.WellbeingGraphView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
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
            WellbeingQuestionDao mockQuestionDao = mock(WellbeingQuestionDao.class);
            WellbeingRecordDao mockWellbeingDao = mock(WellbeingRecordDao.class);

            SurveyResponse[] responses = new SurveyResponse[] {
                new SurveyResponse(new GregorianCalendar(1972, 3, 29).getTimeInMillis(), WaysToWellbeing.CONNECT, "A survey title", "A survey description"),
                new SurveyResponse(new GregorianCalendar(1999, 2, 29).getTimeInMillis(), WaysToWellbeing.UNASSIGNED, "A survey title", "Another survey description")
            };

            when(mockWellbeingDao.getDataBySurvey(anyLong())).thenReturn(Collections.singletonList(new RawSurveyData(357457, "Survey note", "Activity note", "Activity name", 1, "Question", 1, true, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString(), -1, -1)));

            LiveData<List<WellbeingGraphItem>> graphDataLive = new MutableLiveData<>(Arrays.asList());
            when(mockQuestionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong())).thenReturn(graphDataLive);

            when(mockQuestionDao.getWaysToWellbeingBetweenTimesNotLive(anyLong(), anyLong())).thenReturn(Arrays.asList(new WellbeingGraphItem("CONNECT", 100), new WellbeingGraphItem("BE_ACTIVE", 50), new WellbeingGraphItem("KEEP_LEARNING", 70), new WellbeingGraphItem("TAKE_NOTICE", 40), new WellbeingGraphItem("GIVE", 20)));

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(mockSurveyDao.getLiveInsights(anyString()))
                    .thenReturn(wayToWellbeing);

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(Arrays.asList(responses));

            when(mockSurveyDao.getAllSurveyResponses()).thenReturn(data);
            when(mockSurveyDao.getHistoryPageData()).thenReturn(Arrays.asList(responses));
            when(mockSurveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong())).thenReturn(data);
            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(mockQuestionDao);
            when(mockWellbeingDatabase.wellbeingRecordDao()).thenReturn(mockWellbeingDao);

            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(mockSurveyDao);

            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setUp() throws InterruptedException {
        hiltTest.inject();
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void whenDataInTheDatabase_TextShouldBeDisplayed() {
        onView(withId(R.id.navigation_view_survey_responses)).perform(click());

        // Check that the image is displayed
        onView(withId(R.id.surveyRecyclerView))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(allOf(withId(R.id.survey_list_title), withText("29 Apr 1972"))))))
            .check(matches(atRecyclerPosition(0, hasDescendant(allOf(withId(R.id.survey_list_description), withText("A survey description"))))))
            .check(matches(atRecyclerPosition(0, hasDescendant(allOf(withId(R.id.view_more_button), withText("View more"))))))
            .check(matches(atRecyclerPosition(0, hasDescendant(withId(R.id.surveys_completed_frame_layout)))))
            .check(matches(atRecyclerPosition(0, hasDescendant(instanceOf(ImageView.class)))));
            // The graph always seems to exist, even if it isn't visible
            // The image only exists if it has been created


        // Check that the graph is displayed and that the image is not
        onView(withId(R.id.surveyRecyclerView))
            .perform(scrollToPosition(1))
            .check(matches(atRecyclerPosition(1, hasDescendant(allOf(withId(R.id.survey_list_title), withText("29 Mar 1999"))))))
            .check(matches(atRecyclerPosition(1, hasDescendant(allOf(withId(R.id.survey_list_description), withText("Another survey description"))))))
            .check(matches(atRecyclerPosition(1, hasDescendant(allOf(withId(R.id.view_more_button), withText("View more"))))))
            .check(matches(atRecyclerPosition(1, hasDescendant(withId(R.id.surveys_completed_frame_layout)))))
            .check(matches(atRecyclerPosition(1, hasDescendant(instanceOf(WellbeingGraphView.class)))))
            .check(matches(atRecyclerPosition(1, not(hasDescendant(instanceOf(ImageView.class))))));
    }
}
