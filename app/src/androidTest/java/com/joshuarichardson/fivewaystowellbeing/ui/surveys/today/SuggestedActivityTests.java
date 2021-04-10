package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.ProgressFragmentTestFixture;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.PhysicalActivity;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

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
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.LinearLayoutTestUtil.nthChildOf;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class SuggestedActivityTests  extends ProgressFragmentTestFixture {

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

        when(this.activityRecordDao.getActivityRecordById(1)).thenReturn(new ActivityRecord("Run", 2000, 36776324, ActivityType.EXERCISE, WaysToWellbeing.BE_ACTIVE, false));
        when(this.activityRecordDao.getActivityRecordById(2)).thenReturn(new ActivityRecord("Cycle", 2000, 36776324, ActivityType.EXERCISE, WaysToWellbeing.BE_ACTIVE, false));

        when(this.surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong())).thenReturn(new MutableLiveData<>(
            Collections.singletonList(new SurveyResponse(67864242, WaysToWellbeing.UNASSIGNED, "Survey", "")))
        );

        when(this.physicalActivityDao.getPending()).thenReturn(Arrays.asList(
            new PhysicalActivity("RUN", null, 1617238800000L, 1617242400000L, 1, true, false),
            new PhysicalActivity("CYCLE", null, 1617238800000L, 1617242400000L, 2, true, false)
        ));
    }

    @Test
    public void pendingItemsShouldBeDisplayed() {
        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Run"))));

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Cycle"))));
    }

    @Test
    public void clickingOnItems_ShouldChangeWhatIsDisplayed() {
        onView(allOf(withId(R.id.yes_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo(), click());

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), withText("Run"))));

        onView(allOf(withId(R.id.no_button), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 0))))
            .perform(scrollTo(), click());

        onView(allOf(withId(R.id.activity_text), isDescendantOfA(nthChildOf(withId(R.id.survey_item_container), 1))))
            .check(doesNotExist());
    }
}
