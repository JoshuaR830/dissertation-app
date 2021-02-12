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
    private long surveyActivityId;
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

        long surveyResponseId1 = surveyResponseDao.insert(new SurveyResponse(5146253, WaysToWellbeing.CONNECT, "Title", "Survey note "));
        long activityRecordId = activityRecordDao.insert(new ActivityRecord("Activity name", 2000, 34625476, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE));
        SurveyResponseActivityRecord surveyResponseActivityRecord = new SurveyResponseActivityRecord(surveyResponseId1, activityRecordId, 0, "Activity note", 123456, 234567);
        this.surveyActivityId = surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord);

        // Set up the questions
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(1, "question 1", "Positive message 1", "Negative message 1", WaysToWellbeing.CONNECT.toString(), 1, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(2, "question 2", "Positive message 2", "Negative message 2", WaysToWellbeing.BE_ACTIVE.toString(), 2, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(3, "question 3", "Positive message 3", "Negative message 3", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(4, "question 4", "Positive message 4", "Negative message 4", WaysToWellbeing.TAKE_NOTICE.toString(), 4, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.wellbeingQuestionDao.insert(new WellbeingQuestion(5, "question 5", "Positive message 5", "Negative message 5", WaysToWellbeing.GIVE.toString(), 5, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
    }

    @Test
    public void whenAllWellbeingTypesPresent_AllValuesShouldGreaterThanZero() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 1, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 0, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 1, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 0, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 1, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 0, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 1, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 0, 5));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 1, 5));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(5);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(2);
        assertThat(graphData.getBeActiveValue()).isEqualTo(4);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(6);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(8);
        assertThat(graphData.getGiveValue()).isEqualTo(10);
    }

    @Test
    public void whenNoWellbeingTypesPresent_AllValuesShouldBeZero() throws TimeoutException, InterruptedException {
        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

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
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 1, 1));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(1);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(2);
        assertThat(graphData.getBeActiveValue()).isEqualTo(0);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(0);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenCheckboxNotSelected_ValuesShouldNotBeIncluded() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 467356, surveyActivityId, 1, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 2, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 3, 1));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(1);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(2);
        assertThat(graphData.getBeActiveValue()).isEqualTo(0);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(0);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenAllUserInputsFalse_AllValuesShouldBeZero() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 1, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 0, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 1, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 0, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 1, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 0, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 1, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 0, 5));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 467356, surveyActivityId, 1, 5));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(0);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(0);
        assertThat(graphData.getBeActiveValue()).isEqualTo(0);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(0);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }

    @Test
    public void whenOutOfTimeRange_NoValuesShouldBeReturned() throws TimeoutException, InterruptedException {
        wellbeingRecordDao.insert(new WellbeingRecord(true, 500, surveyActivityId, 0, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 500, surveyActivityId, 1, 1));

        List<WellbeingGraphItem> graphItem = LiveDataTestUtil.getOrAwaitValue(this.wellbeingQuestionDao.getWaysToWellbeingBetweenTimes(467356, 467356));

        assertThat(graphItem.size()).isEqualTo(0);

        WellbeingGraphValueHelper graphData = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItem);

        assertThat(graphData.getConnectValue()).isEqualTo(0);
        assertThat(graphData.getBeActiveValue()).isEqualTo(0);
        assertThat(graphData.getKeepLearningValue()).isEqualTo(0);
        assertThat(graphData.getTakeNoticeValue()).isEqualTo(0);
        assertThat(graphData.getGiveValue()).isEqualTo(0);
    }
}
