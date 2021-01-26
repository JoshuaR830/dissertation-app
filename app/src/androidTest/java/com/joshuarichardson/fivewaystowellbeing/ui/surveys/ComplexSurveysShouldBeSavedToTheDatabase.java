package com.joshuarichardson.fivewaystowellbeing.ui.surveys;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.AnswerSurveyActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class ComplexSurveysShouldBeSavedToTheDatabase {
    // ToDo - implement the tests for testing questions
    private SurveyResponseDao surveyDao;
    private SurveyResponseElementDao surveyResponseElementDao;

    @Captor
    ArgumentCaptor<SurveyResponseElement> surveyResponseElementCaptor = ArgumentCaptor.forClass(SurveyResponseElement.class);

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public ActivityScenarioRule<AnswerSurveyActivity> answerSurveyActivity = new ActivityScenarioRule<>(AnswerSurveyActivity.class);

    @Rule
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Inject
    LogAnalyticEventHelper helper;

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);

            // Mocks to make the test work
            ActivityRecordDao activityDao = mock(ActivityRecordDao.class);
            ComplexSurveysShouldBeSavedToTheDatabase.this.surveyDao = mock(SurveyResponseDao.class);
            when(ComplexSurveysShouldBeSavedToTheDatabase.this.surveyDao.insert(any(SurveyResponse.class))).thenReturn(3L);
            ArrayList<ActivityRecord> activityList = new ArrayList<>();
            activityList.add(new ActivityRecord("Activity", 2000, 736284628, ActivityType.APP));
            when(activityDao.getAllActivitiesNotLive()).thenReturn(activityList);
            when(mockWellbeingDatabase.activityRecordDao()).thenReturn(activityDao);
            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(ComplexSurveysShouldBeSavedToTheDatabase.this.surveyDao);

            // What the test actually is interested in
            ComplexSurveysShouldBeSavedToTheDatabase.this.surveyResponseElementDao = mock(SurveyResponseElementDao.class);
            when(ComplexSurveysShouldBeSavedToTheDatabase.this.surveyResponseElementDao.insert(any(SurveyResponseElement.class))).thenReturn(0L);
            when(mockWellbeingDatabase.surveyResponseElementDao()).thenReturn(ComplexSurveysShouldBeSavedToTheDatabase.this.surveyResponseElementDao);

            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setup() {
        hiltTest.inject();
    }

    @Test
    public void onSubmit_SurveyElementsShouldBeSavedToDatabase() {
        onView(allOf(withId(R.id.text_input), isDescendantOfA(withId(0)))).perform(scrollTo(), typeText("Question 1 response"), closeSoftKeyboard());

        onView(withId(R.id.submitButton))
                .perform(click());

        verify(this.surveyResponseElementDao, times(1))
            .insert(this.surveyResponseElementCaptor.capture());

        SurveyResponseElement testSubject = this.surveyResponseElementCaptor.getAllValues().get(0);
        assertThat(testSubject.getAnswer()).isEqualTo("Question 1 response");
        assertThat(testSubject.getSurveyId()).isEqualTo(3);
    }
}
