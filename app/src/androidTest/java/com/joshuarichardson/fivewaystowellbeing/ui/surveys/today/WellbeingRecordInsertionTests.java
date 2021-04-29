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
import com.joshuarichardson.fivewaystowellbeing.surveys.ActivityInstance;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


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

        doReturn(activityData)
            .when(activityRecordDao)
            .getAllActivities();

        LiveData<List<SurveyResponse>> data = new MutableLiveData<>(Collections.singletonList(new SurveyResponse(12345, WaysToWellbeing.UNASSIGNED, "Title", "Note")));

        doReturn(data).when(WellbeingRecordInsertionTests.this.surveyDao)
            .getSurveyResponsesByTimestampRange(anyLong(), anyLong());


        doReturn(Collections.singletonList(new WellbeingQuestion(1, "question 1", "+ve 1", "-ve 1", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString())))
            .when(WellbeingRecordInsertionTests.this.questionDao)
            .getQuestionsByActivityType(ActivityType.LEARNING.toString());

        doReturn(
            Arrays.asList(
                new WellbeingQuestion(1, "question 1", "+ve 1", "-ve 1", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
                new WellbeingQuestion(2, "question 2", "+ve 2", "-ve 2", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
                new WellbeingQuestion(3, "question 3", "+ve 3", "-ve 3", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
                new WellbeingQuestion(4, "question 4", "+ve 4", "-ve 4", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString())
        ))
            .when(WellbeingRecordInsertionTests.this.questionDao)
            .getQuestionsByActivityType(ActivityType.HOBBY.toString());

        doReturn(1L)
            .when(WellbeingRecordInsertionTests.this.questionDao)
            .insert(any(WellbeingQuestion.class));
    }

    @Test
    public void testInsertingWhereNoQuestionsOfType_ShouldInsertNoQuestions() throws InterruptedException {
        WellbeingRecordInsertionHelper.addActivityToSurvey(this.mockWellbeingDatabase, 1, ActivityType.SPORT.toString());
        WellbeingDatabaseModule.databaseExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.SPORT.toString());
        verify(this.wellbeingDao, times(0)).insert(any(WellbeingRecord.class));
    }

    @Test
    public void testInsertingOneQuestionOfType_ShouldInsertOneQuestion() throws InterruptedException {
        WellbeingRecordInsertionHelper.addActivityToSurvey(this.mockWellbeingDatabase, 2, ActivityType.LEARNING.toString());
        WellbeingDatabaseModule.databaseExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.LEARNING.toString());
        verify(this.wellbeingDao, times(1)).insert(any(WellbeingRecord.class));
    }

    @Test
    public void testInsertingFourQuestionsOfType_ShouldInsertFourQuestions() throws InterruptedException {
        WellbeingRecordInsertionHelper.addActivityToSurvey(this.mockWellbeingDatabase, 3, ActivityType.HOBBY.toString());
        WellbeingDatabaseModule.databaseExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.HOBBY.toString());
        verify(this.wellbeingDao, times(4)).insert(any(WellbeingRecord.class));
    }

    @Test
    public void testInsertingWhereNoQuestionsOfType_ShouldInsertNoQuestionsAndActivityShouldHaveNoQuestions() throws InterruptedException {
        ActivityInstance activityInstance = WellbeingRecordInsertionHelper.addActivityQuestions(this.mockWellbeingDatabase, 1, ActivityType.SPORT.toString(), new ActivityInstance("name", "note", "SPORT", WaysToWellbeing.BE_ACTIVE.toString(), 1, -1, -1, 0, false), 0);
        WellbeingDatabaseModule.databaseExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.SPORT.toString());
        verify(this.wellbeingDao, times(0)).insert(any(WellbeingRecord.class));

        assertThat(activityInstance.getQuestions()).hasSize(0);
    }

    @Test
    public void testInsertingOneQuestionOfType_ShouldInsertOneQuestionAndActivityShouldHaveOneQuestion() throws InterruptedException {
        ActivityInstance activityInstance = WellbeingRecordInsertionHelper.addActivityQuestions(this.mockWellbeingDatabase, 2, ActivityType.LEARNING.toString(), new ActivityInstance("name", "note", "LEARNING", WaysToWellbeing.KEEP_LEARNING.toString(), 1, -1, -1, 0, false), 0);
        WellbeingDatabaseModule.databaseExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.LEARNING.toString());
        verify(this.wellbeingDao, times(1)).insert(any(WellbeingRecord.class));

        assertThat(activityInstance.getQuestions()).hasSize(1);
    }

    @Test
    public void testInsertingFourQuestionsOfType_ShouldInsertFourQuestionsAndActivityShouldHaveFourQuestions() throws InterruptedException {
        ActivityInstance activityInstance = WellbeingRecordInsertionHelper.addActivityQuestions(this.mockWellbeingDatabase, 3, ActivityType.HOBBY.toString(), new ActivityInstance("name", "note", "HOBBY", WaysToWellbeing.KEEP_LEARNING.toString(), 1, -1, -1, 0, false), 0);
        verify(this.questionDao, times(1)).getQuestionsByActivityType(ActivityType.HOBBY.toString());
        verify(this.wellbeingDao, times(4)).insert(any(WellbeingRecord.class));

        assertThat(activityInstance.getQuestions()).hasSize(4);
    }
}
