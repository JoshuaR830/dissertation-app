package com.joshuarichardson.fivewaystowellbeing.ui.surveys;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.AnswerSurveyActivity;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.QuestionsToAskDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyQuestionSetDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.BASIC_SURVEY;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private SurveyQuestionSetDao surveyQuestionsDao;

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {
        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
            ActivityRecordDao activityDao = mock(ActivityRecordDao.class);
            QuestionsToAskDao questionsToAskDao = mock(QuestionsToAskDao.class);
            BasicSurveyInfoShouldBeSavedToTheDatabase.this.surveyQuestionsDao = mock(SurveyQuestionSetDao.class);

            // Set the data for the questions to ask
            List<QuestionsToAsk> questionsToAsk = Arrays.asList(new QuestionsToAsk("N/A", "N/A", 1, BASIC_SURVEY.name(), 0, null));
//            MutableLiveData<List<QuestionsToAsk>> liveQuestionsToAsk = new MutableLiveData<>(questionsToAsk);
            when(questionsToAskDao.getQuestionsBySetId(anyLong())).thenReturn(questionsToAsk);

            // Set the data to return for unanswered surveys
            SurveyQuestionSet[] surveyQuestionList = new SurveyQuestionSet[] {new SurveyQuestionSet(485798345, 0)};
            List<SurveyQuestionSet> surveyQuestionSets = Arrays.asList(surveyQuestionList);
//            MutableLiveData<List<SurveyQuestionSet>> liveSurveyQuestionSets = new MutableLiveData<>(surveyQuestionSets);
            when(BasicSurveyInfoShouldBeSavedToTheDatabase.this.surveyQuestionsDao.getUnansweredSurveyQuestionSets()).thenReturn(surveyQuestionSets);

            when(mockWellbeingDatabase.questionsToAskDao()).thenReturn(questionsToAskDao);
            when(mockWellbeingDatabase.surveyQuestionSetDao()).thenReturn(BasicSurveyInfoShouldBeSavedToTheDatabase.this.surveyQuestionsDao);

            ArrayList<ActivityRecord> activityList = new ArrayList<>();
            activityList.add(new ActivityRecord("Activity", 2000, 736284628, ActivityType.APP));
            when(activityDao.getAllActivitiesNotLive()).thenReturn(activityList);

            BasicSurveyInfoShouldBeSavedToTheDatabase.this.surveyDao = mock(SurveyResponseDao.class);

            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(BasicSurveyInfoShouldBeSavedToTheDatabase.this.surveyDao);
            when(mockWellbeingDatabase.activityRecordDao()).thenReturn(activityDao);

            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setup() {
        hiltTest.inject();
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

        verify(this.surveyQuestionsDao, times(1))
                .updateSetWithCompletedSurveyId(anyLong(), anyLong());
    }
}
