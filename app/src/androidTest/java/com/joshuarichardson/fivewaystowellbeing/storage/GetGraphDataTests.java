package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;
import com.joshuarichardson.fivewaystowellbeing.utilities.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class GetGraphDataTests {
    private WellbeingQuestionDao wellbeingQuestionDao;
    private long surveyActivityId1;
    private long surveyActivityId2;
    private long surveyActivityId3;
    private WellbeingRecordDao wellbeingRecordDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();
        WellbeingDatabase wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        SurveyResponseDao surveyResponseDao = wellbeingDb.surveyResponseDao();
        ActivityRecordDao activityRecordDao = wellbeingDb.activityRecordDao();
        this.wellbeingRecordDao = wellbeingDb.wellbeingRecordDao();
        this.wellbeingQuestionDao = wellbeingDb.wellbeingQuestionDao();
        SurveyResponseActivityRecordDao surveyResponseActivityRecordDao = wellbeingDb.surveyResponseActivityRecordDao();

        long surveyResponseId1 = surveyResponseDao.insert(new SurveyResponse(467356, WaysToWellbeing.CONNECT, "Title 1", "Survey note 1"));
        long surveyResponseId2 = surveyResponseDao.insert(new SurveyResponse(500, WaysToWellbeing.CONNECT, "Title 2", "Survey note 2"));
        long activityRecordId1 = activityRecordDao.insert(new ActivityRecord("Activity name 1", 2000, 34625476, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE));
        long activityRecordId2 = activityRecordDao.insert(new ActivityRecord("Activity name 2", 2000, 34625476, ActivityType.LEARNING, WaysToWellbeing.KEEP_LEARNING));
        SurveyResponseActivityRecord surveyResponseActivityRecord1 = new SurveyResponseActivityRecord(surveyResponseId1, activityRecordId1, 0, "Activity note", 123456, 234567, 1);
        SurveyResponseActivityRecord surveyResponseActivityRecord2 = new SurveyResponseActivityRecord(surveyResponseId1, activityRecordId2, 0, "Activity note", 123456, 234567, 1);
        SurveyResponseActivityRecord surveyResponseActivityRecord3 = new SurveyResponseActivityRecord(surveyResponseId2, activityRecordId2, 0, "Activity note", 123456, 234567, 1);
        this.surveyActivityId1 = surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord1);
        this.surveyActivityId2 = surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord2);
        this.surveyActivityId3 = surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord3);

        // Set up the questions
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(1, "question 1", "Positive message 1", "Negative message 1", WaysToWellbeing.CONNECT.toString(), 1, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(2, "question 2", "Positive message 2", "Negative message 2", WaysToWellbeing.BE_ACTIVE.toString(), 2, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(3, "question 3", "Positive message 3", "Negative message 3", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(4, "question 4", "Positive message 4", "Negative message 4", WaysToWellbeing.TAKE_NOTICE.toString(), 4, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(5, "question 5", "Positive message 5", "Negative message 5", WaysToWellbeing.GIVE.toString(), 5, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
    }

    @Test
    public void whenAllWellbeingTypesPresent_AllValuesShouldGreaterThanZero() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 1, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 0, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 1, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 0, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 1, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 0, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 1, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 0, 5));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 1, 5));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(5);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(2);
        assertThat(graphData.getBeActiveValue()).isEqualTo(54);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(56);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(8);
        assertThat(graphData.getGiveValue()).isEqualTo(10);
    }

    @Test
    public void whenNoWellbeingTypesPresent_AllValuesShouldBeZero() throws TimeoutException, InterruptedException {
        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467357, 467357));

        assertThat(graphItem.size()).isEqualTo(0);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(0);
        assertThat(graphData.getBeActiveValue()).isEqualTo(0);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(0);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenOnlyOneWellbeingTypeIsPresent_AllValuesShouldBeZeroExceptForTheValue() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 1, 1));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(3);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(2);
        assertThat(graphData.getBeActiveValue()).isEqualTo(50);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(50);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenCheckboxNotSelected_ValuesShouldNotBeIncluded() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 1, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 2, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 3, 1));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(3);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(2);
        assertThat(graphData.getBeActiveValue()).isEqualTo(50);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(50);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenAllUserInputsFalse_AllValuesShouldBeZero() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 1, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 0, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 1, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 0, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 1, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 0, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 1, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 0, 5));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId1, 1, 5));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(2);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(0);
        assertThat(graphData.getBeActiveValue()).isEqualTo(50);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(50);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenOutOfTimeRange_NoValuesShouldBeReturned() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 500, surveyActivityId1, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 500, surveyActivityId1, 1, 1));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(2);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(0);
        assertThat(graphData.getBeActiveValue()).isEqualTo(50);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(50);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenActivitiesSelected_TheyShouldCountTowardsTheValues() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId2, 1, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId1, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId2, 1, 1));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(3);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(4);
        assertThat(graphData.getBeActiveValue()).isEqualTo(50);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(50);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenSurveyIsBeforeTimeSelected_ShouldNotBeCountedButQuestionsThatAreInTimeRangeShould() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467357, surveyActivityId3, 0, 1));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467357, 467357));

        assertThat(graphItem.size()).isEqualTo(1);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(1);
        assertThat(graphData.getBeActiveValue()).isEqualTo(0);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(0);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenSurveyAndQuestionsAreBeforeTimeSelected_ShouldNotBeCountedButQuestionsThatAreInTimeRangeShould() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 500, surveyActivityId3, 0, 1));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467357, 467357));

        assertThat(graphItem.size()).isEqualTo(0);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(0);
        assertThat(graphData.getBeActiveValue()).isEqualTo(0);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(0);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }
}
