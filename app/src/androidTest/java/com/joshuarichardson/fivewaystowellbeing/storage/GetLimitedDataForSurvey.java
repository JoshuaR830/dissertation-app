package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class GetLimitedDataForSurvey {
    private WellbeingDatabase wellbeingDb;
    private SurveyResponseDao surveyResponseDao;
    private ActivityRecordDao activityRecordDao;
    private WellbeingRecordDao wellbeingRecordDao;
    private SurveyResponseActivityRecordDao surveyResponseActivityRecordDao;
    private long surveyResponseId1;
    private long surveyResponseId2;
    private long surveyResponseId3;

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
        this.surveyResponseActivityRecordDao = this.wellbeingDb.surveyResponseActivityRecordDao();

        SurveyResponse surveyResponse = new SurveyResponse(5146253, WaysToWellbeing.CONNECT, "Title", "Survey note 1");
        this.surveyResponseId1 = this.surveyResponseDao.insert(surveyResponse);
        this.surveyResponseId2 = this.surveyResponseDao.insert(surveyResponse);
        this.surveyResponseId3 = this.surveyResponseDao.insert(surveyResponse);

        ActivityRecord activityRecord1 = new ActivityRecord("Activity name 1", 2000, 34625476, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE);
        ActivityRecord activityRecord2 = new ActivityRecord("Activity name 2", 2000, 34625477, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE);
        ActivityRecord activityRecord3 = new ActivityRecord("Activity name 3", 2000, 34625478, ActivityType.SPORT, WaysToWellbeing.BE_ACTIVE);

        long activityRecordId2 = this.activityRecordDao.insert(activityRecord2);
        long activityRecordId3 = this.activityRecordDao.insert(activityRecord3);
        long activityRecordId1 = this.activityRecordDao.insert(activityRecord1);


        SurveyResponseActivityRecord surveyResponseActivityRecord1_1 = new SurveyResponseActivityRecord(surveyResponseId1, activityRecordId1, 0, null, 0, 0, 1);

        SurveyResponseActivityRecord surveyResponseActivityRecord2_1 = new SurveyResponseActivityRecord(surveyResponseId2, activityRecordId1, 0, null, 0, 0, 1);
        SurveyResponseActivityRecord surveyResponseActivityRecord2_2 = new SurveyResponseActivityRecord(surveyResponseId2, activityRecordId2, 0, null, 0, 0, 1);
        SurveyResponseActivityRecord surveyResponseActivityRecord2_3 = new SurveyResponseActivityRecord(surveyResponseId2, activityRecordId3, 0, null, 0, 0, 1);

        // This has the same activity id so that the question order can be tested
        SurveyResponseActivityRecord surveyResponseActivityRecord3_1 = new SurveyResponseActivityRecord(surveyResponseId3, activityRecordId3, 0, null, 0, 0, 1);

        // sA1 is for testing that correct response is provided
        this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord1_1);

        // sA2 is for testing activities by sequence - hence inserted in wrong order - requires multiple survey activities so that order can be tested
        this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord2_3);
        this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord2_1);
        this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord2_2);

        // sA3 is to ensure that there are no questions
        this.surveyResponseActivityRecordDao.insert(surveyResponseActivityRecord3_1);
    }

    @Test
    public void gettingDataBySurvey_ShouldReturnCorrectValues() {
        List<LimitedRawSurveyData> data = this.wellbeingRecordDao.getLimitedDataBySurvey(this.surveyResponseId1);

        assertThat(data.size()).isEqualTo(1);
        assertThat(data.get(0).getDate()).isEqualTo(5146253);
        assertThat(data.get(0).getSurveyNote()).isEqualTo("Survey note 1");
        assertThat(data.get(0).getActivityName()).isEqualTo("Activity name 1");
        assertThat(data.get(0).getActivityNote()).isEqualTo(null);
        assertThat(data.get(0).getSurveyActivityId()).isEqualTo(-1);
        assertThat(data.get(0).getQuestion()).isEqualTo(null);
        assertThat(data.get(0).getUserInput()).isEqualTo(false);
    }

    @Test
    public void getDataBySurvey_ShouldReturnNoQuestions() {
        List<LimitedRawSurveyData> data = this.wellbeingRecordDao.getLimitedDataBySurvey(this.surveyResponseId3);

        assertThat(data.size()).isEqualTo(1);
        assertThat(data.get(0).getQuestion()).isEqualTo(null);
    }

    @Test
    public void getDataBySurvey_ShouldReturnActivitiesInTimeOrder() {
        List<LimitedRawSurveyData> data = this.wellbeingRecordDao.getLimitedDataBySurvey(this.surveyResponseId2);

        assertThat(data.size()).isEqualTo(3);
        assertThat(data.get(0).getActivityName()).isEqualTo("Activity name 1");
        assertThat(data.get(1).getActivityName()).isEqualTo("Activity name 2");
        assertThat(data.get(2).getActivityName()).isEqualTo("Activity name 3");
    }
}
