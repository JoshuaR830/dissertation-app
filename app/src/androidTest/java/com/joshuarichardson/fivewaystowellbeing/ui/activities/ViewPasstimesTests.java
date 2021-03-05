package com.joshuarichardson.fivewaystowellbeing.ui.activities;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit.ViewPassTimesActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.joshuarichardson.fivewaystowellbeing.utilities.MaterialComponentTestUtil.withMaterialHint;
import static com.joshuarichardson.fivewaystowellbeing.utilities.RecyclerViewTestUtil.atRecyclerPosition;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class ViewPasstimesTests {
    @Rule(order = 0)
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule(order = 1)
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule(order = 2)
    public ActivityScenarioRule<ViewPassTimesActivity> viewPasstimesActivity = new ActivityScenarioRule<>(ViewPassTimesActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        hiltTest.inject();
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase db = mock(WellbeingDatabase.class);
            ActivityRecordDao activityDao = mock(ActivityRecordDao.class);

            List<ActivityRecord> data = Arrays.asList(
                    new ActivityRecord("Activity name 1", 0, 587468, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE, false),
                    new ActivityRecord("Activity name 2", 0, 587468, ActivityType.LEARNING, WaysToWellbeing.KEEP_LEARNING, false),
                    new ActivityRecord("Activity name 3", 0, 587468, ActivityType.HOBBY, WaysToWellbeing.KEEP_LEARNING, false)
            );
            LiveData<List<ActivityRecord>> liveData = new MutableLiveData<>(data);

            when(activityDao.getAllActivities()).thenReturn(liveData);
            when(db.activityRecordDao()).thenReturn(activityDao);

            return db;
        }
    }

    @Test
    public void onThePasstimeList_allPasstimesShouldBeDisplayed() {
        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(withText("Activity name 1")))));

        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(1))
            .check(matches(atRecyclerPosition(1, hasDescendant(withText("Activity name 2")))));

        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(2))
            .check(matches(atRecyclerPosition(2, hasDescendant(withText("Activity name 3")))));

        onView(withId(R.id.create_from_search_button))
            .check(matches(not(isDisplayed())));
    }

    @Test
    public void onThePasstimeList_TheSearchBoxShouldBeShown() {
        onView(withId(R.id.passtime_search_box_container))
            .check(matches(withMaterialHint("Search for activity")));

        onView(withId(R.id.passtime_search_box))
            .check(matches(isDisplayed()));
    }

    @Test
    public void onSearchForKnownPasstime_TheItemShouldBeDisplayedFirst() {
        onView(withId(R.id.passtime_search_box))
            .perform(typeText("Activity name 3"), closeSoftKeyboard());

        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(withText("Activity name 3")))));

        onView(withId(R.id.create_from_search_button))
            .check(matches(not(isDisplayed())));
    }

    @Test
    public void onSearchForPartialKnownPasstime_TheItemShouldBeDisplayedFirst() {
        onView(withId(R.id.passtime_search_box))
            .perform(typeText("3"), closeSoftKeyboard());

        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(withText("Activity name 3")))));

        onView(withId(R.id.create_from_search_button))
            .check(matches(not(isDisplayed())));
    }

    @Test
    public void onSearchForMultiPartialKnownPasstime_TheItemShouldBeDisplayedFirst() {
        onView(withId(R.id.passtime_search_box))
            .perform(typeText("name 3"), closeSoftKeyboard());

        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(0))
            .check(matches(atRecyclerPosition(0, hasDescendant(withText("Activity name 3")))));

        onView(withId(R.id.create_from_search_button))
            .check(matches(not(isDisplayed())));
    }

    @Test
    public void onSearchForUnknownPasstime_ACreateButtonShouldBeDisplayed() throws InterruptedException {
        onView(withId(R.id.passtime_search_box))
            .perform(typeText("New activity"));

        onView(withId(R.id.passTimeRecyclerView))
            .check(matches(not(isDisplayed())));

        onView(withId(R.id.create_from_search_button))
            .check(matches(isDisplayed()))
            .perform(click());

        onView(withId(R.id.pass_time_name_input))
            .check(matches(withText("New activity")));

        onView(withId(R.id.pass_time_type_input)).perform(click());
        onData(instanceOf(String.class))
            .inRoot(RootMatchers.isPlatformPopup())
            .atPosition(0)
            .perform(click());

        onView(withId(R.id.passtime_submit_button)).perform(click());
    }
}