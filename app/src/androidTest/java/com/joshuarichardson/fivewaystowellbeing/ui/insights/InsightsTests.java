package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.ProgressFragmentTestFixture;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.ActivityStats;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class InsightsTests extends ProgressFragmentTestFixture {
    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {

            // Set up the default items to return
            defaultResponses();

            // Return the DAOs from the DB
            mockDatabaseResponses();

            return mockWellbeingDatabase;
        }
    }

    @Override
    protected void defaultResponses() {
        super.defaultResponses();

        // Return the activities
        doReturn(new ActivityRecord("Talk", 453876587, 784687, "PEOPLE", "CONNECT", false))
            .when(activityRecordDao)
            .getActivityRecordById(1);

        doReturn(new ActivityRecord("Place holder", 453876587, 784687, "SPORT", "BE_ACTIVE", false))
            .when(activityRecordDao)
            .getActivityRecordById(2);

        doReturn(new ActivityRecord("Washing up", 453876587, 784687, "CHORES", "GIVE", false))
            .when(activityRecordDao)
            .getActivityRecordById(3);

        when(questionDao.getWaysToWellbeingBetweenTimesNotLive(anyLong(), anyLong())).thenReturn(Arrays.asList(new WellbeingGraphItem(WaysToWellbeing.CONNECT.toString(), 140), new WellbeingGraphItem(WaysToWellbeing.BE_ACTIVE.toString(), 90), new WellbeingGraphItem(WaysToWellbeing.KEEP_LEARNING.toString(), 120), new WellbeingGraphItem(WaysToWellbeing.TAKE_NOTICE.toString(), 100), new WellbeingGraphItem(WaysToWellbeing.GIVE.toString(), 20)));

        when(wellbeingDao.getDataBySurvey(anyLong())).thenReturn(Collections.singletonList(new RawSurveyData(357457, "Survey note", "Activity note", "Activity name", 1, "Question", 1, true, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString(), -1, -1, 0, false)));

        // Return activities by frequency
        doReturn(Arrays.asList(new ActivityStats(1, 3), new ActivityStats(2, 2), new ActivityStats(2, 1)))
            .when(surveyResponseActivityDao)
            .getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("CONNECT"));

        doReturn(Arrays.asList(new ActivityStats(2, 3), new ActivityStats(2, 2), new ActivityStats(2, 1)))
            .when(surveyResponseActivityDao)
            .getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("BE_ACTIVE"));

        doReturn(Arrays.asList(new ActivityStats(2, 9), new ActivityStats(2, 4), new ActivityStats(2, 1)))
            .when(surveyResponseActivityDao)
            .getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("KEEP_LEARNING"));

        doReturn(Arrays.asList(new ActivityStats(2, 7), new ActivityStats(2, 5), new ActivityStats(2, 2)))
            .when(surveyResponseActivityDao)
            .getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("TAKE_NOTICE"));

        doReturn(Arrays.asList(new ActivityStats(2, 7), new ActivityStats(2, 4), new ActivityStats(3, 1)))
            .when(surveyResponseActivityDao)
            .getActivityFrequencyByWellbeingTypeBetweenTimes(anyLong(), anyLong(), eq("GIVE"));

        doReturn(3).when(surveyDao).getNumDaysWithWaysToWellbeingByDate(anyLong(), anyLong());

        List<WellbeingResult> wellbeingResults = Arrays.asList(
            new WellbeingResult(1, 12345, 100, 100, 100, 10, 20),
            new WellbeingResult(2, 23456, 100, 100, 30, 100, 20),
            new WellbeingResult(3, 34567, 100, 20, 30, 10, 20)
        );

        doReturn(wellbeingResults)
            .when(resultsDao)
            .getResultsByTimestampRange(anyLong(), anyLong());

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
            .check(matches(atRecyclerPosition(2, hasDescendant(withText("You are doing best at Connect")))))
            .check(matches(atRecyclerPosition(2, hasDescendant(withText("Favourite activity: Talk")))))
            .check(matches(atRecyclerPosition(2, hasDescendant(withText("You did this regularly over the last week - keep it up!")))));

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(3))
            .check(matches(atRecyclerPosition(3, hasDescendant(withText("You could work on your Give score")))))
            .check(matches(atRecyclerPosition(3, hasDescendant(withText("Suggested activity: Washing up")))))
            .check(matches(atRecyclerPosition(3, hasDescendant(withText("Doing this more regularly will help to boost your daily Give score")))));

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

        onView(withId(R.id.insights_recycler_view))
            .perform(scrollToPosition(9))
            .check(matches(atRecyclerPosition(9, hasDescendant(withText("Times achieved:")))))
            .check(matches(atRecyclerPosition(9, hasDescendant(withText("Days logged")))))
            .check(matches(atRecyclerPosition(9, hasDescendant(withText("3")))));
    }
}
