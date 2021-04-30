package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.ProgressFragmentTestFixture;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;
import com.joshuarichardson.fivewaystowellbeing.utilities.LinearLayoutTestUtil;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class GraphShouldBeDisplayed extends ProgressFragmentTestFixture {

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

        doReturn(Collections.singletonList(new RawSurveyData(357457, "Survey note", "Activity note", "Activity name", 1, "Question", 1, true, ActivityType.HOBBY.toString(), WaysToWellbeing.KEEP_LEARNING.toString(), -1, -1, 0, false)))
            .when(this.wellbeingDao)
            .getDataBySurvey(anyLong());

        List<WellbeingQuestion> trueList = Arrays.asList(
            new WellbeingQuestion(1, "", "Positive message 1", "Negative message 1", WaysToWellbeing.CONNECT.toString(), 1, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(2, "", "Positive message 2", "Negative message 2", WaysToWellbeing.CONNECT.toString(), 1, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(3, "", "Positive message 3", "Negative message 3", WaysToWellbeing.CONNECT.toString(), 1, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString())
        );

        List<WellbeingQuestion> falseList = Arrays.asList(
            new WellbeingQuestion(4, "", "Positive message 4", "Negative message 4", WaysToWellbeing.CONNECT.toString(), 1, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(5, "", "Positive message 5", "Negative message 5", WaysToWellbeing.CONNECT.toString(), 1, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString())
        );

        doReturn(new MutableLiveData<>(trueList))
            .when(this.wellbeingDao)
            .getTrueWellbeingRecordsByTimestampRangeAndWayToWellbeingType(anyLong(), anyLong(), anyString());

        doReturn(new MutableLiveData<>(falseList))
            .when(this.wellbeingDao)
            .getFalseWellbeingRecordsByTimestampRangeAndWayToWellbeingType(anyLong(), anyLong(), anyString());
    }

    @Test
    public void whenOnFirstPage_ThenAGraphShouldBeDisplayedToUsers() {
        // Check that the graph is displayed
        onView(withId(R.id.graph_card_container))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(withId(R.id.graph_card))
            .perform(scrollTo())
            .check(matches(isDisplayed()));
    }

    @Test
    public void whenChipClicked_InsightsShouldBeShown() {
        onView(withId(R.id.chip_connect))
            .perform(scrollTo(), click());

        onView(withId(R.id.way_to_wellbeing_help_container))
            .perform(scrollTo())
            .check(matches(hasChildCount(3)));

        onView(withId(R.id.help_card_title))
            .perform(scrollTo())
            .check(matches(withText("Connect")));

        onView(allOf(withId(R.id.progress_insight_title), isDescendantOfA(LinearLayoutTestUtil.nthChildOf(withId(R.id.way_to_wellbeing_help_container), 1))))
            .perform(scrollTo())
            .check(matches(withText("Great job!")));

        // Should be the second item
        onView(allOf(withId(R.id.item_container), isDescendantOfA(LinearLayoutTestUtil.nthChildOf(withId(R.id.way_to_wellbeing_help_container), 1))))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.item_container), isDescendantOfA(LinearLayoutTestUtil.nthChildOf(withId(R.id.way_to_wellbeing_help_container), 1))))
            .perform(scrollTo())
            .check(matches(hasChildCount(3)));

        // Should be the third item
        onView(allOf(withId(R.id.item_container), isDescendantOfA(LinearLayoutTestUtil.nthChildOf(withId(R.id.way_to_wellbeing_help_container), 2))))
            .perform(scrollTo())
            .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.progress_insight_title), isDescendantOfA(LinearLayoutTestUtil.nthChildOf(withId(R.id.way_to_wellbeing_help_container), 2))))
            .perform(scrollTo())
            .check(matches(withText("Suggestions")));

        onView(allOf(withId(R.id.item_container), isDescendantOfA(LinearLayoutTestUtil.nthChildOf(withId(R.id.way_to_wellbeing_help_container), 2))))
            .perform(scrollTo())
            .check(matches(hasChildCount(2)));
    }
}
