package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.MainActivity;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.Passtime;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.WellbeingRecordInsertionHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class WellbeingRecordInsertionTests {
    private WellbeingDatabase mockWellbeingDatabase;
    private SurveyResponseDao surveyDao;
    private WellbeingQuestionDao questionDao;
    private WellbeingRecordDao wellbeingDao;
    private SurveyResponseActivityRecordDao surveyResponseActivityDao;

    @Rule(order = 0)
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule(order = 1)
    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);

    @Rule(order = 2)
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);

    @Module
    @InstallIn(ApplicationComponent.class)
    public class TestWellbeingDatabaseModule {

        @Provides
        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
            WellbeingRecordInsertionTests.this.mockWellbeingDatabase = mock(WellbeingDatabase.class);

            WellbeingRecordInsertionTests.this.surveyDao = mock(SurveyResponseDao.class);
            WellbeingRecordInsertionTests.this.questionDao = mock(WellbeingQuestionDao.class);
            WellbeingRecordInsertionTests.this.wellbeingDao = mock(WellbeingRecordDao.class);
            WellbeingRecordInsertionTests.this.surveyResponseActivityDao = mock(SurveyResponseActivityRecordDao.class);
            ActivityRecordDao activitiesDao = mock(ActivityRecordDao.class);

            LiveData<List<ActivityRecord>> activityData = new MutableLiveData<>(
                Arrays.asList(
                    new ActivityRecord("Activity 1", 2000, 123456, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE),
                    new ActivityRecord("Activity 2", 3000, 437724, ActivityType.HOBBY, WaysToWellbeing.KEEP_LEARNING)
                )
            );

            when(activitiesDao.getAllActivities()).thenReturn(activityData);

            LiveData<List<SurveyResponse>> data = new MutableLiveData<>(Collections.singletonList(new SurveyResponse(12345, WaysToWellbeing.UNASSIGNED, "Title", "Note")));
            when(WellbeingRecordInsertionTests.this.surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
                .thenReturn(data);

            LiveData<List<WellbeingGraphItem>> graphData = new MutableLiveData<>(Arrays.asList());
            when(WellbeingRecordInsertionTests.this.questionDao.getWaysToWellbeingBetweenTimes(anyLong(), anyLong()))
                .thenReturn(graphData);

            when(WellbeingRecordInsertionTests.this.questionDao.getQuestionsByActivityType(ActivityType.SPORT.toString()))
                .thenReturn(new ArrayList<>());

            when(WellbeingRecordInsertionTests.this.questionDao.getQuestionsByActivityType(ActivityType.LEARNING.toString()))
                .thenReturn(Collections.singletonList(new WellbeingQuestion(1, "question 1", "+ve 1", "-ve 1", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString())));

            when(WellbeingRecordInsertionTests.this.questionDao.getQuestionsByActivityType(ActivityType.HOBBY.toString()))
                .thenReturn(
                    Arrays.asList(
                        new WellbeingQuestion(1, "question 1", "+ve 1", "-ve 1", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
                        new WellbeingQuestion(2, "question 2", "+ve 2", "-ve 2", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
                        new WellbeingQuestion(3, "question 3", "+ve 3", "-ve 3", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
                        new WellbeingQuestion(4, "question 4", "+ve 4", "-ve 4", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString())
                    )
                );

            LiveData<Integer> wayToWellbeing = new MutableLiveData<>();
            when(WellbeingRecordInsertionTests.this.surveyDao.getLiveInsights(anyString()))
                .thenReturn(wayToWellbeing);

            when(WellbeingRecordInsertionTests.this.questionDao.insert(any(WellbeingQuestion.class))).thenReturn(1L);

            when(WellbeingRecordInsertionTests.this.wellbeingDao.getDataBySurvey(anyLong())).thenReturn(new ArrayList<>());

            when(mockWellbeingDatabase.wellbeingRecordDao()).thenReturn(WellbeingRecordInsertionTests.this.wellbeingDao);
            when(mockWellbeingDatabase.wellbeingQuestionDao()).thenReturn(WellbeingRecordInsertionTests.this.questionDao);
            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(WellbeingRecordInsertionTests.this.surveyDao);
            when(mockWellbeingDatabase.surveyResponseActivityRecordDao()).thenReturn(WellbeingRecordInsertionTests.this.surveyResponseActivityDao);
            when(mockWellbeingDatabase.activityRecordDao()).thenReturn(activitiesDao);
            return mockWellbeingDatabase;
        }
    }

    @Before
    public void setUp() throws InterruptedException {
        hiltTest.inject();
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testInsertingWhereNoQuestionsOfType_ShouldInsertNoQuestions() throws InterruptedException {
        WellbeingRecordInsertionHelper.addPasstimeToSurvey(this.mockWellbeingDatabase, 1, ActivityType.SPORT.toString());
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.SPORT.toString());
        verify(this.wellbeingDao, times(0)).insert(any(WellbeingRecord.class));
    }

    @Test
    public void testInsertingOneQuestionOfType_ShouldInsertOneQuestion() throws InterruptedException {
        WellbeingRecordInsertionHelper.addPasstimeToSurvey(this.mockWellbeingDatabase, 2, ActivityType.LEARNING.toString());
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.LEARNING.toString());
        verify(this.wellbeingDao, times(1)).insert(any(WellbeingRecord.class));
    }

    @Test
    public void testInsertingFourQuestionsOfType_ShouldInsertFourQuestions() throws InterruptedException {
        WellbeingRecordInsertionHelper.addPasstimeToSurvey(this.mockWellbeingDatabase, 3, ActivityType.HOBBY.toString());
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.HOBBY.toString());
        verify(this.wellbeingDao, times(4)).insert(any(WellbeingRecord.class));
    }

    @Test
    public void testInsertingWhereNoQuestionsOfType_ShouldInsertNoQuestionsAndPasstimeShouldHaveNoQuestions() throws InterruptedException {
        Passtime passtime = WellbeingRecordInsertionHelper.addPasstimeQuestions(this.mockWellbeingDatabase, 1, ActivityType.SPORT.toString(), new Passtime("name", "note", "SPORT", WaysToWellbeing.BE_ACTIVE.toString(), 1, -1, -1));
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.SPORT.toString());
        verify(this.wellbeingDao, times(0)).insert(any(WellbeingRecord.class));

        assertThat(passtime.getQuestions()).hasSize(0);
    }

    @Test
    public void testInsertingOneQuestionOfType_ShouldInsertOneQuestionAndPasstimeShouldHaveOneQuestion() throws InterruptedException {
        Passtime passtime = WellbeingRecordInsertionHelper.addPasstimeQuestions(this.mockWellbeingDatabase, 2, ActivityType.LEARNING.toString(), new Passtime("name", "note", "LEARNING", WaysToWellbeing.KEEP_LEARNING.toString(), 1, -1, -1));
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.LEARNING.toString());
        verify(this.wellbeingDao, times(1)).insert(any(WellbeingRecord.class));

        assertThat(passtime.getQuestions()).hasSize(1);
    }

    @Test
    public void testInsertingFourQuestionsOfType_ShouldInsertFourQuestionsAndPasstimeShouldHaveFourQuestions() throws InterruptedException {
        Passtime passtime = WellbeingRecordInsertionHelper.addPasstimeQuestions(this.mockWellbeingDatabase, 3, ActivityType.HOBBY.toString(), new Passtime("name", "note", "HOBBY", WaysToWellbeing.KEEP_LEARNING.toString(), 1, -1, -1));
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.HOBBY.toString());
        verify(this.wellbeingDao, times(4)).insert(any(WellbeingRecord.class));

        assertThat(passtime.getQuestions()).hasSize(4);
    }
}
