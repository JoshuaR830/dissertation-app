package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.utilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.fail;

public class SurveyResponseActivityRecordTests {

    private WellbeingDatabase wellbeingDb;
    private SurveyResponseActivityRecordDao surveyActivityDao;
    private ActivityRecordDao activityRecordDao;
    private SurveyResponseDao surveyResponseDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // Need to initialise an in memory room database
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room
                .inMemoryDatabaseBuilder(context, WellbeingDatabase.class)
                .build();

        this.surveyActivityDao = this.wellbeingDb.surveyResponseActivityRecordDao();
        this.activityRecordDao = this.wellbeingDb.activityRecordDao();
        this.surveyResponseDao = this.wellbeingDb.surveyResponseDao();
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void insertionOfActivityRecordIdAndSurveyResponseId_AndGetActivitiesForSurvey_ShouldReturnTheActivity() throws TimeoutException, InterruptedException {
        SurveyResponse surveyResponse1 = new SurveyResponse(1607960245, "Be active");
        SurveyResponse surveyResponse2 = new SurveyResponse(1607960245, "Be active");
        ActivityRecord activityRecord1 = new ActivityRecord("Running", 1200, 1607960240, "Sport");
        ActivityRecord activityRecord2 = new ActivityRecord("Sprinting", 1200, 1607960240, "Sport");
        ActivityRecord activityRecord3 = new ActivityRecord("Boating", 1200, 1607960240, "Sport");

        int surveyId1 = (int) this.surveyResponseDao.insert(surveyResponse1);
        int surveyId2 = (int) this.surveyResponseDao.insert(surveyResponse2);
        int activityId1 = (int) this.activityRecordDao.insert(activityRecord1);
        int activityId2 = (int) this.activityRecordDao.insert(activityRecord2);
        int activityId3 = (int) this.activityRecordDao.insert(activityRecord3);

        SurveyResponseActivityRecord record1 = new SurveyResponseActivityRecord(surveyId1, activityId1);
        SurveyResponseActivityRecord record2 = new SurveyResponseActivityRecord(surveyId2, activityId2);
        SurveyResponseActivityRecord record3 = new SurveyResponseActivityRecord(surveyId2, activityId3);

        this.surveyActivityDao.insert(record1);
        this.surveyActivityDao.insert(record2);
        this.surveyActivityDao.insert(record3);

        List<ActivityRecord> actualRecords = LiveDataTestUtil.getOrAwaitValue(this.surveyActivityDao.getActivitiesBySurveyId(surveyId2));

        assertThat(actualRecords.size()).isEqualTo(2);

        // Order of activities not guaranteed
        assertThat(actualRecords.get(0).getActivityName())
                .isAnyOf("Sprinting", "Boating");

        assertThat(actualRecords.get(0).getActivityRecordId())
                .isAnyOf(activityId2, activityId3);

        assertThat(actualRecords.get(1).getActivityName())
                .isAnyOf("Sprinting", "Boating");

        assertThat(actualRecords.get(1).getActivityRecordId())
                .isAnyOf(activityId2, activityId3);

        // Ensure that they are different to each other
        assertThat(actualRecords.get(0).getActivityName()).isNotEqualTo(actualRecords.get(1).getActivityName());
        assertThat(actualRecords.get(0).getActivityRecordId()).isNotEqualTo(actualRecords.get(1).getActivityRecordId());

    }

    @Test
    public void insertionOfActivityRecordIdAndSurveyResponseId_AndGetSurveyForActivity_ShouldReturnTheSurvey() throws TimeoutException, InterruptedException {
        // Create an insert a survey response
        SurveyResponse surveyResponse1 = new SurveyResponse(1607960245, WaysToWellbeing.TAKE_NOTICE);
        SurveyResponse surveyResponse2 = new SurveyResponse(1607960246, WaysToWellbeing.KEEP_LEARNING);
        SurveyResponse surveyResponse3 = new SurveyResponse(1607960247, WaysToWellbeing.GIVE);
        SurveyResponse surveyResponse4 = new SurveyResponse(1607960248, WaysToWellbeing.BE_ACTIVE);
        SurveyResponse surveyResponse5 = new SurveyResponse(1607960249, WaysToWellbeing.CONNECT);

        int surveyId1 = (int) this.surveyResponseDao.insert(surveyResponse1);
        int surveyId2 = (int) this.surveyResponseDao.insert(surveyResponse2);
        int surveyId3 = (int) this.surveyResponseDao.insert(surveyResponse3);
        int surveyId4 = (int) this.surveyResponseDao.insert(surveyResponse4);
        int surveyId5 = (int) this.surveyResponseDao.insert(surveyResponse5);

        // Create and insert an activity record
        ActivityRecord activityRecord1 = new ActivityRecord("Throwing", 1200, 1607960240, "Sport");
        ActivityRecord activityRecord2 = new ActivityRecord("Fishing", 1200, 1607960240, "Sport");

        int activityId1 = (int) this.activityRecordDao.insert(activityRecord1);
        int activityId2 = (int) this.activityRecordDao.insert(activityRecord2);

        SurveyResponseActivityRecord record1 = new SurveyResponseActivityRecord(surveyId1, activityId1);
        SurveyResponseActivityRecord record2 = new SurveyResponseActivityRecord(surveyId2, activityId1);
        SurveyResponseActivityRecord record3 = new SurveyResponseActivityRecord(surveyId3, activityId2);
        SurveyResponseActivityRecord record4 = new SurveyResponseActivityRecord(surveyId4, activityId2);
        SurveyResponseActivityRecord record5 = new SurveyResponseActivityRecord(surveyId5, activityId2);

        this.surveyActivityDao.insert(record1);
        this.surveyActivityDao.insert(record2);
        this.surveyActivityDao.insert(record3);
        this.surveyActivityDao.insert(record4);
        this.surveyActivityDao.insert(record5);

        List<SurveyResponse> actualRecords = LiveDataTestUtil.getOrAwaitValue(this.surveyActivityDao.getSurveyByActivityId(activityId2));

        assertThat(actualRecords.size()).isEqualTo(3);

        // Check that the correct responses are returned
        assertThat(actualRecords.get(0).getSurveyResponseWayToWellbeing()).isAnyOf(WaysToWellbeing.GIVE.name(), WaysToWellbeing.BE_ACTIVE.name(), WaysToWellbeing.CONNECT.name());
        assertThat(actualRecords.get(0).getSurveyResponseId()).isAnyOf(surveyId3, surveyId4, surveyId5);

        assertThat(actualRecords.get(1).getSurveyResponseWayToWellbeing()).isAnyOf(WaysToWellbeing.GIVE.name(), WaysToWellbeing.BE_ACTIVE.name(), WaysToWellbeing.CONNECT.name());
        assertThat(actualRecords.get(1).getSurveyResponseId()).isAnyOf(surveyId3, surveyId4, surveyId5);

        assertThat(actualRecords.get(2).getSurveyResponseWayToWellbeing()).isAnyOf(WaysToWellbeing.GIVE.name(), WaysToWellbeing.BE_ACTIVE.name(), WaysToWellbeing.CONNECT.name());
        assertThat(actualRecords.get(2).getSurveyResponseId()).isAnyOf(surveyId3, surveyId4, surveyId5);

        // Check that none of the responses are the same as each other
        assertThat(actualRecords.get(0).getSurveyResponseWayToWellbeing()).isNotEqualTo(actualRecords.get(1).getSurveyResponseWayToWellbeing());
        assertThat(actualRecords.get(0).getSurveyResponseWayToWellbeing()).isNotEqualTo(actualRecords.get(2).getSurveyResponseWayToWellbeing());
        assertThat(actualRecords.get(1).getSurveyResponseWayToWellbeing()).isNotEqualTo(actualRecords.get(0).getSurveyResponseWayToWellbeing());
        assertThat(actualRecords.get(1).getSurveyResponseWayToWellbeing()).isNotEqualTo(actualRecords.get(2).getSurveyResponseWayToWellbeing());
        assertThat(actualRecords.get(2).getSurveyResponseWayToWellbeing()).isNotEqualTo(actualRecords.get(0).getSurveyResponseWayToWellbeing());
        assertThat(actualRecords.get(2).getSurveyResponseWayToWellbeing()).isNotEqualTo(actualRecords.get(1).getSurveyResponseWayToWellbeing());

        assertThat(actualRecords.get(0).getSurveyResponseId()).isNotEqualTo(actualRecords.get(1).getSurveyResponseId());
        assertThat(actualRecords.get(0).getSurveyResponseId()).isNotEqualTo(actualRecords.get(2).getSurveyResponseId());
        assertThat(actualRecords.get(1).getSurveyResponseId()).isNotEqualTo(actualRecords.get(0).getSurveyResponseId());
        assertThat(actualRecords.get(1).getSurveyResponseId()).isNotEqualTo(actualRecords.get(2).getSurveyResponseId());
        assertThat(actualRecords.get(2).getSurveyResponseId()).isNotEqualTo(actualRecords.get(0).getSurveyResponseId());
        assertThat(actualRecords.get(2).getSurveyResponseId()).isNotEqualTo(actualRecords.get(1).getSurveyResponseId());
    }

    @Test
    public void whenBothSurveyResponseAndActivityExist_NoExceptionShouldBeThrown() {
        SurveyResponse surveyResponse = new SurveyResponse(1607960245, WaysToWellbeing.TAKE_NOTICE);
        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);

        ActivityRecord activityRecord = new ActivityRecord("Throwing", 1200, 1607960240, "Sport");
        int activityId = (int) this.activityRecordDao.insert(activityRecord);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(surveyId, activityId);

        // As both surveyResponse and activityRecord exist it should not throw an exception
        try {
            this.surveyActivityDao.insert(record);
        } catch(Exception e) {
            fail("No exception should be thrown");
        }
    }

    @Test
    public void insertingASurveyIdAndActivityIdWhereNeitherExist_ShouldThrowAConstraintException() {
        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(112233, 33221);

        Exception exception = new Exception();

        // If no exception is thrown then the test should fail because it is checking that an exception is thrown when no surveys/activities exist
        try {
            this.surveyActivityDao.insert(record);
            fail("Should have thrown an SQLiteConstraintException exception");
        } catch(Exception e) {
            exception = e;
        }

        assertWithMessage("Inserting activity/survey that don't exist should violate constraints")
                .that(exception)
                .isInstanceOf(SQLiteConstraintException.class);
    }

    @Test
    public void insertingASurveyIdWhichDoesNotExist_ShouldThrowAConstraintException() {
        // Create and insert a real activity
        ActivityRecord activityResult = new ActivityRecord("Running", 1200, 1607960240, ActivityType.SPORT);
        int activityRecordId = (int) this.activityRecordDao.insert(activityResult);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(112233, activityRecordId);

        // Test that exception is thrown if no activity exists
        Exception exception = new Exception();
        try {
            this.surveyActivityDao.insert(record);
            fail("Should have thrown an SQLiteConstraintException exception");
        } catch(Exception e) {
            exception = e;
        }

        assertWithMessage("Inserting a survey Id of a survey response that doesn't exist should violate constraints")
                .that(exception)
                .isInstanceOf(SQLiteConstraintException.class);
    }

    @Test
    public void insertingAnActionIdWhichDoesNotExist_ShouldThrowAConstraintException() {
        // Add a survey to the database
        SurveyResponse surveyResponse = new SurveyResponse(1607960245, "Be active");
        int surveyResponseId = (int) this.surveyResponseDao.insert(surveyResponse);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(surveyResponseId, 332211);

        // Check that an exception is raised when inserting - constraints should be set
        Exception exception = new Exception();
        try {
            this.surveyActivityDao.insert(record);
            fail("Should have thrown an SQLiteConstraintException exception");
        } catch(Exception e) {
            exception = e;
        }

        // If constraints not met, then should throw
        assertWithMessage("Inserting an activity Id of a activity record that doesn't exist should violate constraints")
                .that(exception)
                .isInstanceOf(SQLiteConstraintException.class);
    }
}
