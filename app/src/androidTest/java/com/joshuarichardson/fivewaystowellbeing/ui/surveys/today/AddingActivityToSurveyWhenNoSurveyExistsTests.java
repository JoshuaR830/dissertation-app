package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.ProgressFragmentTestFixture;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.contrib.RecyclerViewActions;
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
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class AddingActivityToSurveyWhenNoSurveyExistsTests extends ProgressFragmentTestFixture {

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
        LiveData<List<ActivityRecord>> activityData = new MutableLiveData<>(
            Arrays.asList(
                new ActivityRecord("Activity 1", 2000, 123456, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE, false),
                new ActivityRecord("Activity 2", 3000, 437724, ActivityType.HOBBY, WaysToWellbeing.KEEP_LEARNING, false)
            )
        );
        when(activityRecordDao.getAllActivities()).thenReturn(activityData);

        when(AddingActivityToSurveyWhenNoSurveyExistsTests.this.surveyResponseActivityDao.insert(any(SurveyResponseActivityRecord.class)))
            .thenReturn(1L);
    }

    @Test
    public void whenASurveyExists_ShouldNotInsertANewOneThenShouldBeAbleToAddAnActivityToIt() throws InterruptedException {
        // When no survey exists there should be an insertion
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        verify(this.surveyDao, Mockito.atLeast(1)).getSurveyResponsesByTimestampRange(anyLong(), anyLong());
        verify(this.surveyDao, Mockito.atLeast(1)).insert(any(SurveyResponse.class));

        // Start on main activity - this should launch the passtime view
        onView(withId(R.id.add_activity_button))
            .perform(scrollTo(), click());

        // This should select an item from the activity
        onView(withId(R.id.passTimeRecyclerView))
            .perform(scrollToPosition(0))
            .check(matches(isDisplayed()))
            .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }
}
