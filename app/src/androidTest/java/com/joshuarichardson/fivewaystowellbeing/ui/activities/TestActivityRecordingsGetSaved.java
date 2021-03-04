package com.joshuarichardson.fivewaystowellbeing.ui.activities;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit.CreatePassTimeActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class TestActivityRecordingsGetSaved {
    WellbeingDatabase mockWellbeingDatabase;
    ActivityRecordDao activityDao;

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule
    public ActivityScenarioRule<CreatePassTimeActivity> answerSurveyActivity = new ActivityScenarioRule<>(CreatePassTimeActivity.class);

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
            TestActivityRecordingsGetSaved.this.mockWellbeingDatabase = mock(WellbeingDatabase.class);
            TestActivityRecordingsGetSaved.this.activityDao = mock(ActivityRecordDao.class);
            when(TestActivityRecordingsGetSaved.this.activityDao.insert(any(ActivityRecord.class))).thenReturn(0L);
            when(TestActivityRecordingsGetSaved.this.mockWellbeingDatabase.activityRecordDao()).thenReturn(TestActivityRecordingsGetSaved.this.activityDao);

            return TestActivityRecordingsGetSaved.this.mockWellbeingDatabase;
        }
    }

    @Test
    public void passTimeSubmission_ShouldCallTheDatabase() {
        onView(withId(R.id.pass_time_name_input)).perform(typeText("Activity Name"), closeSoftKeyboard());

        // Select the first option from the drop down menu
        onView(withId(R.id.pass_time_type_input)).perform(click());
        onData(instanceOf(String.class))
                .inRoot(RootMatchers.isPlatformPopup())
                .atPosition(0)
                .perform(click());

        onView(withId(R.id.passtime_submit_button)).perform(click());

        // Check that the insert method is called
        verify(this.activityDao, times(1)).insert(any(ActivityRecord.class));
    }
}
