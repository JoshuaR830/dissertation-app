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
    public void insertionOfActivitySurvey_ThenGettingBySurveyId_ShouldReturnAllDetails() {
        SurveyResponse surveyResponse1 = new SurveyResponse(1607960245, "Be active", "title", "description");
        SurveyResponse surveyResponse2 = new SurveyResponse(1607960245, "Be active", "title", "description");
        ActivityRecord activityRecord1 = new ActivityRecord("Running", 1200, 1607960240, "Sport", "UNASSIGNED");
        ActivityRecord activityRecord2 = new ActivityRecord("Sprinting", 1200, 1607960240, "Sport", "UNASSIGNED");
        ActivityRecord activityRecord3 = new ActivityRecord("Boating", 1200, 1607960240, "Sport", "UNASSIGNED");

        long surveyId1 = this.surveyResponseDao.insert(surveyResponse1);
        long surveyId2 = this.surveyResponseDao.insert(surveyResponse2);
        long activityId1 = this.activityRecordDao.insert(activityRecord1);
        long activityId2 = this.activityRecordDao.insert(activityRecord2);
        long activityId3 = this.activityRecordDao.insert(activityRecord3);

        SurveyResponseActivityRecord record1 = new SurveyResponseActivityRecord(surveyId1, activityId1, 1, "note 1",  1612427791, 1612427795, 1);
        SurveyResponseActivityRecord record2 = new SurveyResponseActivityRecord(surveyId2, activityId2, 2, "note 2",  1712427792, 1712427796, 1);
        SurveyResponseActivityRecord record3 = new SurveyResponseActivityRecord(surveyId2, activityId3, 1, "note 3",  1812427793, 1812427797, 1);

        this.surveyActivityDao.insert(record1);
        this.surveyActivityDao.insert(record2);
        this.surveyActivityDao.insert(record3);

        List<SurveyResponseActivityRecord> actualRecords = this.surveyActivityDao.getActivityRecordDetailsForSurvey(surveyId2);

        assertThat(actualRecords.size()).isEqualTo(2);

        assertThat(actualRecords.get(0).getSurveyActivityId())
            .isEqualTo(activityId3);

        assertThat(actualRecords.get(0).getSequenceNumber())
            .isEqualTo(1);

        assertThat(actualRecords.get(0).getSurveyResponseId())
            .isEqualTo(surveyId2);

        assertThat(actualRecords.get(0).getStartTime())
            .isEqualTo(1812427793);

        assertThat(actualRecords.get(0).getEndTime())
            .isEqualTo(1812427797);

        assertThat(actualRecords.get(0).getNote())
            .isEqualTo("note 3");

        assertThat(actualRecords.get(1).getSurveyActivityId())
            .isEqualTo(activityId2);

        assertThat(actualRecords.get(1).getSequenceNumber())
            .isEqualTo(2);

        assertThat(actualRecords.get(1).getSurveyResponseId())
            .isEqualTo(surveyId2);

        assertThat(actualRecords.get(1).getStartTime())
            .isEqualTo(1712427792);

        assertThat(actualRecords.get(1).getEndTime())
            .isEqualTo(1712427796);

        assertThat(actualRecords.get(1).getNote())
            .isEqualTo("note 2");
    }

    @Test
    public void insertionOfActivityRecordIdAndSurveyResponseId_AndGetActivitiesForSurvey_ShouldReturnTheActivitiesInSequenceOrder() throws TimeoutException, InterruptedException {
        SurveyResponse surveyResponse1 = new SurveyResponse(1607960245, "Be active", "title", "description");
        SurveyResponse surveyResponse2 = new SurveyResponse(1607960245, "Be active", "title", "description");
        ActivityRecord activityRecord1 = new ActivityRecord("Running", 1200, 1607960240, "Sport", "UNASSIGNED");
        ActivityRecord activityRecord2 = new ActivityRecord("Sprinting", 1200, 1607960240, "Sport", "UNASSIGNED");
        ActivityRecord activityRecord3 = new ActivityRecord("Boating", 1200, 1607960240, "Sport", "UNASSIGNED");

        long surveyId1 = this.surveyResponseDao.insert(surveyResponse1);
        long surveyId2 = this.surveyResponseDao.insert(surveyResponse2);
        long activityId1 = this.activityRecordDao.insert(activityRecord1);
        long activityId2 = this.activityRecordDao.insert(activityRecord2);
        long activityId3 = this.activityRecordDao.insert(activityRecord3);

        SurveyResponseActivityRecord record1 = new SurveyResponseActivityRecord(surveyId1, activityId1, 1, "note 1",  1612427791, 1612427795, 1);
        SurveyResponseActivityRecord record2 = new SurveyResponseActivityRecord(surveyId2, activityId2, 2, "note 2",  1712427792, 1712427796, 1);
        SurveyResponseActivityRecord record3 = new SurveyResponseActivityRecord(surveyId2, activityId3, 1, "note 3",  1812427793, 1812427797, 1);

        this.surveyActivityDao.insert(record1);
        this.surveyActivityDao.insert(record2);
        this.surveyActivityDao.insert(record3);

        List<ActivityRecord> actualRecords = this.surveyActivityDao.getActivitiesBySurveyId(surveyId2);

        assertThat(actualRecords.size()).isEqualTo(2);

        // Ensure the correct response order
        assertThat(actualRecords.get(0).getActivityName())
                .isEqualTo("Boating");

        assertThat(actualRecords.get(0).getActivityRecordId())
                .isEqualTo(activityId3);

        assertThat(actualRecords.get(1).getActivityName())
                .isEqualTo("Sprinting");

        assertThat(actualRecords.get(1).getActivityRecordId())
                .isEqualTo(activityId2);
    }

    @Test
    public void insertionOfActivityRecordIdAndSurveyResponseId_AndGetSurveyForActivity_ShouldReturnTheSurvey() throws TimeoutException, InterruptedException {
        // Create an insert a survey response
        SurveyResponse surveyResponse1 = new SurveyResponse(1607960245, WaysToWellbeing.TAKE_NOTICE, "title", "description");
        SurveyResponse surveyResponse2 = new SurveyResponse(1607960246, WaysToWellbeing.KEEP_LEARNING, "title", "description");
        SurveyResponse surveyResponse3 = new SurveyResponse(1607960247, WaysToWellbeing.GIVE, "title", "description");
        SurveyResponse surveyResponse4 = new SurveyResponse(1607960248, WaysToWellbeing.BE_ACTIVE, "title", "description");
        SurveyResponse surveyResponse5 = new SurveyResponse(1607960249, WaysToWellbeing.CONNECT, "title", "description");

        long surveyId1 = this.surveyResponseDao.insert(surveyResponse1);
        long surveyId2 = this.surveyResponseDao.insert(surveyResponse2);
        long surveyId3 = this.surveyResponseDao.insert(surveyResponse3);
        long surveyId4 = this.surveyResponseDao.insert(surveyResponse4);
        long surveyId5 = this.surveyResponseDao.insert(surveyResponse5);

        // Create and insert an activity record
        ActivityRecord activityRecord1 = new ActivityRecord("Throwing", 1200, 1607960240, "Sport", "UNASSIGNED");
        ActivityRecord activityRecord2 = new ActivityRecord("Fishing", 1200, 1607960240, "Sport", "UNASSIGNED");

        long activityId1 = this.activityRecordDao.insert(activityRecord1);
        long activityId2 = this.activityRecordDao.insert(activityRecord2);

        SurveyResponseActivityRecord record1 = new SurveyResponseActivityRecord(surveyId1, activityId1, 1, "note 1",  1612427791, 1612427795, 1);
        SurveyResponseActivityRecord record2 = new SurveyResponseActivityRecord(surveyId2, activityId1, 1, "note 1",  1612427791, 1612427795, 1);
        SurveyResponseActivityRecord record3 = new SurveyResponseActivityRecord(surveyId3, activityId2, 1, "note 1",  1612427791, 1612427795, 1);
        SurveyResponseActivityRecord record4 = new SurveyResponseActivityRecord(surveyId4, activityId2, 1, "note 1",  1612427791, 1612427795, 1);
        SurveyResponseActivityRecord record5 = new SurveyResponseActivityRecord(surveyId5, activityId2, 1, "note 1",  1612427791, 1612427795, 1);

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
        SurveyResponse surveyResponse = new SurveyResponse(1607960245, WaysToWellbeing.TAKE_NOTICE, "title", "description");
        long surveyId = this.surveyResponseDao.insert(surveyResponse);

        ActivityRecord activityRecord = new ActivityRecord("Throwing", 1200, 1607960240, "Sport", "UNASSIGNED");
        long activityId = this.activityRecordDao.insert(activityRecord);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(surveyId, activityId, 1, "note 1",  1612427791, 1612427795, 1);

        // As both surveyResponse and activityRecord exist it should not throw an exception
        try {
            this.surveyActivityDao.insert(record);
        } catch(Exception e) {
            fail("No exception should be thrown");
        }
    }

    @Test
    public void insertingASurveyIdAndActivityIdWhereNeitherExist_ShouldThrowAConstraintException() {
        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(112233, 33221, 1, "note 1",  1612427791, 1612427795, 1);

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
        ActivityRecord activityResult = new ActivityRecord("Running", 1200, 1607960240, ActivityType.SPORT, WaysToWellbeing.UNASSIGNED);
        long activityRecordId = this.activityRecordDao.insert(activityResult);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(112233, activityRecordId, 1, "note 1",  1612427791, 1612427795, 1);

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
        SurveyResponse surveyResponse = new SurveyResponse(1607960245, "Be active", "title", "description");
        long surveyResponseId = this.surveyResponseDao.insert(surveyResponse);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(surveyResponseId, 332211, 1, "note 1",  1612427791, 1612427795, 1);

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
