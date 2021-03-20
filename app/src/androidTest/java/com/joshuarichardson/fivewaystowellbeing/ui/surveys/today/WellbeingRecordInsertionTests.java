package com.joshuarichardson.fivewaystowellbeing.ui.surveys.today;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.ProgressFragmentTestFixture;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.Passtime;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.WellbeingRecordInsertionHelper;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@HiltAndroidTest
@UninstallModules(WellbeingDatabaseModule.class)
public class WellbeingRecordInsertionTests extends ProgressFragmentTestFixture {

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

        LiveData<List<SurveyResponse>> data = new MutableLiveData<>(Collections.singletonList(new SurveyResponse(12345, WaysToWellbeing.UNASSIGNED, "Title", "Note")));
        when(WellbeingRecordInsertionTests.this.surveyDao.getSurveyResponsesByTimestampRange(anyLong(), anyLong()))
            .thenReturn(data);

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

        when(WellbeingRecordInsertionTests.this.questionDao.insert(any(WellbeingQuestion.class))).thenReturn(1L);
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
        Passtime passtime = WellbeingRecordInsertionHelper.addPasstimeQuestions(this.mockWellbeingDatabase, 1, ActivityType.SPORT.toString(), new Passtime("name", "note", "SPORT", WaysToWellbeing.BE_ACTIVE.toString(), 1, -1, -1, 0, false), 0);
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.SPORT.toString());
        verify(this.wellbeingDao, times(0)).insert(any(WellbeingRecord.class));

        assertThat(passtime.getQuestions()).hasSize(0);
    }

    @Test
    public void testInsertingOneQuestionOfType_ShouldInsertOneQuestionAndPasstimeShouldHaveOneQuestion() throws InterruptedException {
        Passtime passtime = WellbeingRecordInsertionHelper.addPasstimeQuestions(this.mockWellbeingDatabase, 2, ActivityType.LEARNING.toString(), new Passtime("name", "note", "LEARNING", WaysToWellbeing.KEEP_LEARNING.toString(), 1, -1, -1, 0, false), 0);
        WellbeingDatabaseModule.databaseWriteExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.LEARNING.toString());
        verify(this.wellbeingDao, times(1)).insert(any(WellbeingRecord.class));

        assertThat(passtime.getQuestions()).hasSize(1);
    }

    @Test
    public void testInsertingFourQuestionsOfType_ShouldInsertFourQuestionsAndPasstimeShouldHaveFourQuestions() throws InterruptedException {
        Passtime passtime = WellbeingRecordInsertionHelper.addPasstimeQuestions(this.mockWellbeingDatabase, 3, ActivityType.HOBBY.toString(), new Passtime("name", "note", "HOBBY", WaysToWellbeing.KEEP_LEARNING.toString(), 1, -1, -1, 0, false), 0);
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.HOBBY.toString());
        verify(this.wellbeingDao, times(4)).insert(any(WellbeingRecord.class));

        assertThat(passtime.getQuestions()).hasSize(4);
    }
}
