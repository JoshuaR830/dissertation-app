package com.joshuarichardson.fivewaystowellbeing.ui.surveys;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.AnswerSurveyActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Test that the survey information is entered correctly
@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class BasicSurveyInfoShouldBeSavedToTheDatabase {
    SurveyResponseDao surveyDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public ActivityScenarioRule<AnswerSurveyActivity> answerSurveyActivity = new ActivityScenarioRule<>(AnswerSurveyActivity.class);

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
            ActivityRecordDao activityDao = mock(ActivityRecordDao.class);

            ArrayList<ActivityRecord> activityList = new ArrayList<>();
            activityList.add(new ActivityRecord("Activity", 2000, 736284628, ActivityType.APP));
            when(activityDao.getAllActivitiesNotLive()).thenReturn(activityList);

            BasicSurveyInfoShouldBeSavedToTheDatabase.this.surveyDao = mock(SurveyResponseDao.class);

            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(BasicSurveyInfoShouldBeSavedToTheDatabase.this.surveyDao);
            when(mockWellbeingDatabase.activityRecordDao()).thenReturn(activityDao);

            return mockWellbeingDatabase;
        }
    }

    @Test
    public void onSubmit_SurveysShouldBeSavedToDatabase() {
        onView(withId(R.id.survey_title_input)).perform(scrollTo(), typeText("Title"), closeSoftKeyboard());
        onView(withId(R.id.survey_description_input)).perform(scrollTo(), typeText("Description"), closeSoftKeyboard());
        onView(withId(R.id.survey_activity_input)).perform(scrollTo(), click());

        onData(instanceOf(String.class))
                .inRoot(RootMatchers.isPlatformPopup())
                .atPosition(0)
                .perform(scrollTo(), click());

        onView(withId(R.id.submitButton))
                .perform(click());

        verify(this.surveyDao, times(1))
                .insert(any(SurveyResponse.class));
    }
}
