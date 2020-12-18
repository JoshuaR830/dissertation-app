package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.utilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeoutException;

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
        SurveyResponse surveyResponse = new SurveyResponse(1607960245, "Be active");
        ActivityRecord activityRecord = new ActivityRecord("Running", 1200, 1607960240, "Sport");

        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);
        int activityId = (int) this.activityRecordDao.insert(activityRecord);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(surveyId, activityId);

        this.surveyActivityDao.insert(record);

        List<SurveyResponseActivityRecord> actualRecords = LiveDataTestUtil.getOrAwaitValue(this.surveyActivityDao.getActivitiesBySurveyId());

        assertThat(actualRecords.size()).isGreaterThan(0);

        assertThat(actualRecords.get(0).getSurveyResponseId())
                .isEqualTo(record.getSurveyResponseId());

        assertThat(actualRecords.get(0).getActivityRecordId())
                .isEqualTo(record.getActivityRecordId());
    }

    @Test
    public void insertionOfActivityRecordIdAndSurveyResponseId_AndGetSurveyForActivity_ShouldReturnTheSurvey() throws TimeoutException, InterruptedException {
        // Create an insert a survey response
        SurveyResponse surveyResponse = new SurveyResponse(1607960245, "Be active");
        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);

        // Create and insert an activity record
        ActivityRecord activityRecord = new ActivityRecord("Running", 1200, 1607960240, "Sport");
        int activityId = (int) this.activityRecordDao.insert(activityRecord);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(surveyId, activityId);

        // As both surveyResponse and activityRecord exist it should not throw an exception
        try {
            this.surveyActivityDao.insert(record);
        } catch(Exception e) {
            fail("No exception should be thrown");
        }

        List<SurveyResponseActivityRecord> actualRecords = LiveDataTestUtil.getOrAwaitValue(this.surveyActivityDao.getSurveyByActivityId());

        assertThat(actualRecords.size()).isGreaterThan(0);

        assertThat(actualRecords.get(0).getSurveyResponseId())
                .isEqualTo(record.getSurveyResponseId());

        assertThat(actualRecords.get(0).getActivityRecordId())
                .isEqualTo(record.getActivityRecordId());
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
