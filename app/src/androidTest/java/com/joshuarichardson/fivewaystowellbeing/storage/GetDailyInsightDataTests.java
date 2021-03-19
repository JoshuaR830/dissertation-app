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

public class GetDailyInsightDataTests {
    private WellbeingRecordDao wellbeingRecordDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();

        // Create the database
        WellbeingDatabase wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();

        this.wellbeingRecordDao = wellbeingDb.wellbeingRecordDao();
        WellbeingQuestionDao wellbeingQuestionDao = wellbeingDb.wellbeingQuestionDao();
        SurveyResponseActivityRecordDao surveyResponseActivityRecordDao = wellbeingDb.surveyResponseActivityRecordDao();

        // Insert 2 new surveys
        SurveyResponseDao surveyResponseDao = wellbeingDb.surveyResponseDao();
        long surveyResponseId1 = surveyResponseDao.insert(new SurveyResponse(467356, WaysToWellbeing.CONNECT, "Title 1", "Survey note 1"));
        long surveyResponseId2 = surveyResponseDao.insert(new SurveyResponse(500, WaysToWellbeing.CONNECT, "Title 2", "Survey note 2"));

        ActivityRecordDao activityRecordDao = wellbeingDb.activityRecordDao();
        // Insert 2 new activities
        long activityRecordId1 = activityRecordDao.insert(new ActivityRecord("Activity name 1", 2000, 34625476, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE, false));
        long activityRecordId2 = activityRecordDao.insert(new ActivityRecord("Activity name 2", 2000, 34625476, ActivityType.LEARNING, WaysToWellbeing.KEEP_LEARNING, false));

        // Create 3 new survey activity items
        SurveyResponseActivityRecord surveyResponseActivityRecord1 = new SurveyResponseActivityRecord(surveyResponseId1, activityRecordId1, 0, "Activity note", 123456, 234567, 1, false);
        SurveyResponseActivityRecord surveyResponseActivityRecord2 = new SurveyResponseActivityRecord(surveyResponseId1, activityRecordId2, 0, "Activity note", 123456, 234567, 1, false);
        SurveyResponseActivityRecord surveyResponseActivityRecord3 = new SurveyResponseActivityRecord(surveyResponseId2, activityRecordId2, 0, "Activity note", 123456, 234567, 1, false);

        // Add 3 new survey activities
        long surveyActivityId1 = surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord1);
        long surveyActivityId2 = surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord2);
        long surveyActivityId3 = surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord3);

        // Set up the questions
        wellbeingQuestionDao.insert(new WellbeingQuestion(1, "question 1", "Positive message 1", "Negative message 1", WaysToWellbeing.CONNECT.toString(), 1, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        wellbeingQuestionDao.insert(new WellbeingQuestion(2, "question 2", "Positive message 2", "Negative message 2", WaysToWellbeing.BE_ACTIVE.toString(), 2, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        wellbeingQuestionDao.insert(new WellbeingQuestion(3, "question 3", "Positive message 3", "Negative message 3", WaysToWellbeing.KEEP_LEARNING.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        wellbeingQuestionDao.insert(new WellbeingQuestion(4, "question 4", "Positive message 4", "Negative message 4", WaysToWellbeing.TAKE_NOTICE.toString(), 4, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));
        wellbeingQuestionDao.insert(new WellbeingQuestion(5, "question 5", "Positive message 5", "Negative message 5", WaysToWellbeing.GIVE.toString(), 5, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()));

        wellbeingRecordDao.insert(new WellbeingRecord(true, 1685830, surveyActivityId1, 1, 1));

        wellbeingRecordDao.insert(new WellbeingRecord(false, 5685861, surveyActivityId1, 2, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 5685862, surveyActivityId1, 3, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 5685863, surveyActivityId1, 4, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 5685864, surveyActivityId1, 5, 5));

        wellbeingRecordDao.insert(new WellbeingRecord(true, 5685865, surveyActivityId2, 1, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 5685866, surveyActivityId2, 2, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 5685867, surveyActivityId2, 3, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 5685868, surveyActivityId2, 4, 4));
        wellbeingRecordDao.insert(new WellbeingRecord(true, 5685869, surveyActivityId2, 5, 5));

        wellbeingRecordDao.insert(new WellbeingRecord(false, 5685870, surveyActivityId3, 1, 1));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 5685871, surveyActivityId3, 2, 2));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 5685872, surveyActivityId3, 3, 3));
        wellbeingRecordDao.insert(new WellbeingRecord(false, 5685873, surveyActivityId3, 4, 4));

        wellbeingRecordDao.insert(new WellbeingRecord(false, 9685890, surveyActivityId3, 5, 5));
    }

    @Test
    public void whenGetTrueResultsAndOnlyOne_ShouldBeReturnedInSequenceActivityOrder() throws TimeoutException, InterruptedException {
        // can I just get it between times
        List<WellbeingQuestion> response = LiveDataTestUtil.getOrAwaitValue(this.wellbeingRecordDao.getTrueWellbeingRecordsByTimestampRange(5685861, 5685873, WaysToWellbeing.GIVE.toString()));

        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getPositiveMessage()).isEqualTo("Positive message 5");
    }

    @Test
    public void whenGetTrueResultsAndMultiple_ShouldBeReturnedInSequenceActivityOrder() throws TimeoutException, InterruptedException {
        // can I just get it between times
        List<WellbeingQuestion> response = LiveDataTestUtil.getOrAwaitValue(this.wellbeingRecordDao.getTrueWellbeingRecordsByTimestampRange(5685861, 5685873, WaysToWellbeing.KEEP_LEARNING.toString()));

        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(0).getPositiveMessage()).isEqualTo("Positive message 3");
        assertThat(response.get(1).getPositiveMessage()).isEqualTo("Positive message 3");
    }

    @Test
    public void whenGetTrueResultsAndOneOUtOfRange_ShouldReturnOnlyOne() throws TimeoutException, InterruptedException {
        // can I just get it between times
        List<WellbeingQuestion> response = LiveDataTestUtil.getOrAwaitValue(this.wellbeingRecordDao.getTrueWellbeingRecordsByTimestampRange(5685861, 5685873, WaysToWellbeing.CONNECT.toString()));

        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getPositiveMessage()).isEqualTo("Positive message 1");
    }

    @Test
    public void whenGetFalseResults_ShouldBeReturnedInSequenceActivityOrder() throws TimeoutException, InterruptedException {
        // can I just get it between times
        List<WellbeingQuestion> response = LiveDataTestUtil.getOrAwaitValue(this.wellbeingRecordDao.getFalseWellbeingRecordsByTimestampRange(5685861, 5685873, WaysToWellbeing.BE_ACTIVE.toString()));

        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(0).getNegativeMessage()).isEqualTo("Negative message 2");
        assertThat(response.get(1).getNegativeMessage()).isEqualTo("Negative message 2");
    }

    @Test
    public void whenGetFalseResultsAndSomeOutOfRange_ShouldReturnOneResult() throws TimeoutException, InterruptedException {
        // can I just get it between times
        List<WellbeingQuestion> response = LiveDataTestUtil.getOrAwaitValue(this.wellbeingRecordDao.getFalseWellbeingRecordsByTimestampRange(5685861, 5685873, WaysToWellbeing.GIVE.toString()));

        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getNegativeMessage()).isEqualTo("Negative message 5");
    }

    @Test
    public void whenFalseTimeOutOfRange_NoResultsShouldBeShown() throws TimeoutException, InterruptedException {
        // can I just get it between times
        List<WellbeingQuestion> response = LiveDataTestUtil.getOrAwaitValue(this.wellbeingRecordDao.getFalseWellbeingRecordsByTimestampRange(5685874, 5685874, WaysToWellbeing.BE_ACTIVE.toString()));
        assertThat(response.size()).isEqualTo(0);
    }

    @Test
    public void whenTrueTimeOutOfRange_NoResultsShouldBeShown() throws TimeoutException, InterruptedException {
        // can I just get it between times
        List<WellbeingQuestion> response = LiveDataTestUtil.getOrAwaitValue(this.wellbeingRecordDao.getTrueWellbeingRecordsByTimestampRange(5685874, 5685874, WaysToWellbeing.BE_ACTIVE.toString()));
        assertThat(response.size()).isEqualTo(0);
    }
}
