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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class GetDataBySurveyTests {

    private WellbeingDatabase wellbeingDb;
    private SurveyResponseDao surveyResponseDao;
    private ActivityRecordDao activityRecordDao;
    private WellbeingRecordDao wellbeingRecordDao;
    private WellbeingQuestionDao wellbeingQuestionDao;
    private SurveyResponseActivityRecordDao surveyResponseActivityRecordDao;
    private long surveyResponseId1;
    private long surveyResponseId2;
    private long surveyResponseId3;
    private long surveyActivityId1_1;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.surveyResponseDao = this.wellbeingDb.surveyResponseDao();
        this.activityRecordDao = this.wellbeingDb.activityRecordDao();
        this.wellbeingRecordDao = this.wellbeingDb.wellbeingRecordDao();
        this.wellbeingQuestionDao = this.wellbeingDb.wellbeingQuestionDao();
        this.surveyResponseActivityRecordDao = this.wellbeingDb.surveyResponseActivityRecordDao();

        SurveyResponse surveyResponse = new SurveyResponse(5146253, WaysToWellbeing.CONNECT, "Title", "Survey note 1", 0, 0, 0, 0, 0);
        this.surveyResponseId1 = this.surveyResponseDao.insert(surveyResponse);
        this.surveyResponseId2 = this.surveyResponseDao.insert(surveyResponse);
        this.surveyResponseId3 = this.surveyResponseDao.insert(surveyResponse);

        ActivityRecord activityRecord1 = new ActivityRecord("Activity name 1", 2000, 34625476, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE, false);
        long activityRecordId1 = this.activityRecordDao.insert(activityRecord1);

        ActivityRecord activityRecord2 = new ActivityRecord("Activity name 2", 2000, 34625476, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE, false);
        long activityRecordId2 = this.activityRecordDao.insert(activityRecord2);

        ActivityRecord activityRecord3 = new ActivityRecord("Activity name 3", 2000, 34625476, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE, false);
        long activityRecordId3 = this.activityRecordDao.insert(activityRecord3);

        SurveyResponseActivityRecord surveyResponseActivityRecord1_1 = new SurveyResponseActivityRecord(surveyResponseId1, activityRecordId1, 0, "Activity note 1_1", 123456, 234567, 1, false);

        SurveyResponseActivityRecord surveyResponseActivityRecord2_1 = new SurveyResponseActivityRecord(surveyResponseId2, activityRecordId1, 0, "Activity note 2_1", 123456, 234567, 1, false);
        SurveyResponseActivityRecord surveyResponseActivityRecord2_2 = new SurveyResponseActivityRecord(surveyResponseId2, activityRecordId2, 1, "Activity note 2_2", 123456, 234567, 1, false);
        SurveyResponseActivityRecord surveyResponseActivityRecord2_3 = new SurveyResponseActivityRecord(surveyResponseId2, activityRecordId3, 2, "Activity note 2_3", 123456, 234567, 1, false);

        // This has the same activity id so that the question order can be tested
        SurveyResponseActivityRecord surveyResponseActivityRecord3_1 = new SurveyResponseActivityRecord(surveyResponseId3, activityRecordId3, 1, "Activity note 3_1", 123456, 234567, 1, false);

        // sA1 is for testing that correct response is provided
        this.surveyActivityId1_1 = this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord1_1);

        // sA2 is for testing activities by sequence - hence inserted in wrong order - requires multiple survey activities so that order can be tested
        long surveyActivityId2_3 = this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord2_3);
        long surveyActivityId2_1 = this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord2_1);
        long surveyActivityId2_2 = this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord2_2);

        // sA3 is for testing that questions are returned in the correct order - only needs 1 survey activity - but will contain multiple questions
        long surveyActivityId3_1 = this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord3_1);

        WellbeingQuestion wellbeingQuestion1 = new WellbeingQuestion(1, "question 1", "Positive message 1", "Negative message 1", WaysToWellbeing.CONNECT.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString());
        WellbeingQuestion wellbeingQuestion2 = new WellbeingQuestion(2, "question 2", "Positive message 2", "Negative message 2", WaysToWellbeing.CONNECT.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString());
        WellbeingQuestion wellbeingQuestion3 = new WellbeingQuestion(3, "question 3", "Positive message 3", "Negative message 3", WaysToWellbeing.CONNECT.toString(), 3, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString());

        // Insert these out of order to check that they are ordered by sequence number
        this.wellbeingQuestionDao.insert(wellbeingQuestion3);
        this.wellbeingQuestionDao.insert(wellbeingQuestion1);
        this.wellbeingQuestionDao.insert(wellbeingQuestion2);

        // To check that the correct information is available
        WellbeingRecord wellbeingRecord1_1 = new WellbeingRecord(true, 467356, this.surveyActivityId1_1, 0, 1);

        // To check order of activities are by sequence - hence put in backwards
        WellbeingRecord wellbeingRecord2_1 = new WellbeingRecord(true, 467356, surveyActivityId2_1, 0, 1);
        WellbeingRecord wellbeingRecord2_2 = new WellbeingRecord(true, 467356, surveyActivityId2_2, 0, 1);
        WellbeingRecord wellbeingRecord2_3 = new WellbeingRecord(true, 467356, surveyActivityId2_3, 0, 1);

        // To check order of questions within an activity - hence put in backwards
        WellbeingRecord wellbeingRecord3_1 = new WellbeingRecord(true, 467356, surveyActivityId3_1, 0, 1);
        WellbeingRecord wellbeingRecord3_2 = new WellbeingRecord(true, 467356, surveyActivityId3_1, 1, 2);
        WellbeingRecord wellbeingRecord3_3 = new WellbeingRecord(true, 467356, surveyActivityId3_1, 2, 3);

        // Insert a normal one that can be tested on its own
        this.wellbeingRecordDao.insert(wellbeingRecord1_1);

        // Insert out of order
        this.wellbeingRecordDao.insert(wellbeingRecord3_3);
        this.wellbeingRecordDao.insert(wellbeingRecord3_1);
        this.wellbeingRecordDao.insert(wellbeingRecord3_2);

        // Insert out of order
        this.wellbeingRecordDao.insert(wellbeingRecord2_3);
        this.wellbeingRecordDao.insert(wellbeingRecord2_1);
        this.wellbeingRecordDao.insert(wellbeingRecord2_2);
    }

    @Test
    public void gettingDataBySurvey_ShouldReturnCorrectValues() {
        List<RawSurveyData> data = this.wellbeingRecordDao.getDataBySurvey(this.surveyResponseId1);

        assertThat(data.size()).isEqualTo(1);
        assertThat(data.get(0).getDate()).isEqualTo(5146253);
        assertThat(data.get(0).getSurveyNote()).isEqualTo("Survey note 1");
        assertThat(data.get(0).getActivityName()).isEqualTo("Activity name 1");
        assertThat(data.get(0).getActivityNote()).isEqualTo("Activity note 1_1");
        assertThat(data.get(0).getSurveyActivityId()).isEqualTo(this.surveyActivityId1_1);
        assertThat(data.get(0).getQuestion()).isEqualTo("question 1");
        assertThat(data.get(0).getUserInput()).isEqualTo(true);
    }

    @Test
    public void getDataBySurvey_ShouldReturnQuestionsInCorrectOrder() {
        List<RawSurveyData> data = this.wellbeingRecordDao.getDataBySurvey(this.surveyResponseId3);

        assertThat(data.size()).isEqualTo(3);
        assertThat(data.get(0).getQuestion()).isEqualTo("question 1");
        assertThat(data.get(1).getQuestion()).isEqualTo("question 2");
        assertThat(data.get(2).getQuestion()).isEqualTo("question 3");
    }

    @Test
    public void getDataBySurvey_ShouldReturnActivitiesInCorrectOrder() {
        List<RawSurveyData> data = this.wellbeingRecordDao.getDataBySurvey(this.surveyResponseId2);

        assertThat(data.size()).isEqualTo(3);
        assertThat(data.get(0).getActivityName()).isEqualTo("Activity name 1");
        assertThat(data.get(1).getActivityName()).isEqualTo("Activity name 2");
        assertThat(data.get(2).getActivityName()).isEqualTo("Activity name 3");
    }
}
