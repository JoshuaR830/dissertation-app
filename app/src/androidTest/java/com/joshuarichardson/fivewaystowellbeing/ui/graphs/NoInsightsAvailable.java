package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.ProgressFragmentTestFixture;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.utilities.LinearLayoutTestUtil;

import org.junit.Test;

import java.util.Collections;

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
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class NoInsightsAvailable extends ProgressFragmentTestFixture {

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
    }

    @Test
    public void WhenNoMessages_InsightsShouldBeHidden() {
        onView(withId(R.id.chip_connect))
            .perform(scrollTo(), click());

        onView(allOf(withId(R.id.insight_card_layout), isDescendantOfA(LinearLayoutTestUtil.nthChildOf(withId(R.id.way_to_wellbeing_help_container), 1))))
            .check(matches(not(isDisplayed())));
    }
}
