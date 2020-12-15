package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.fail;
//import static com.google.common.truth.ThrowableSubject.hasCauseThat;

public class SurveyResponseActivityRecordTests {

    private WellbeingDatabase surveyActivityDb;
    private SurveyResponseActivityRecordDao surveyActivityDao;
    private ActivityRecordDao activityRecordDao;
    private SurveyResponseDao surveyResponseDao;

    @Before
    public void setup() {
        // Need to initialise an in memory room database
        Context context = ApplicationProvider.getApplicationContext();
        this.surveyActivityDb = Room
                .inMemoryDatabaseBuilder(context, WellbeingDatabase.class)
                .build();

        this.surveyActivityDao = this.surveyActivityDb.surveyResponseActivityRecordDao();
        this.activityRecordDao = this.surveyActivityDb.activityRecordDao();
        this.surveyResponseDao = this.surveyActivityDb.surveyResponseDao();
    }

    @Test
    public void insertionOfActivityRecordIdAndSurveyResponseId_AndGetActivitiesForSurvey_ShouldReturnTheActivity() {

        // ToDo Create an activity record - get the id insert it
        // ToDo Create a survey response - get id insert it

        SurveyResponse surveyResponse = new SurveyResponse();
        ActivityRecord activityRecord = new ActivityRecord("Running", 1200, 1607960240, "Sport", 0);

        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);
        int activityId = (int) this.activityRecordDao.insert(activityRecord);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(surveyId, activityId);

        int surveyActivityId = (int) this.surveyActivityDao.insert(record);

        List<SurveyResponseActivityRecord> actualRecords = this.surveyActivityDao.getActivitiesBySurveyId();

        assertThat(actualRecords.size()).isGreaterThan(0);

        assertThat(actualRecords.get(0).getSurveyResponseId())
                .isEqualTo(record.getSurveyResponseId());

        assertThat(actualRecords.get(0).getActivityRecordId())
                .isEqualTo(record.getActivityRecordId());
    }

    @Test
    public void insertionOfActivityRecordIdAndSurveyResponseId_AndGetSurveyForActivity_ShouldReturnTheSurvey() {

        // ToDo Create an activity record - get the id insert it
        // ToDo Create a survey response - get id insert it

        SurveyResponse surveyResponse = new SurveyResponse();
        ActivityRecord activityRecord = new ActivityRecord("Running", 1200, 1607960240, "Sport", 0);

        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);
        int activityId = (int) this.activityRecordDao.insert(activityRecord);

        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(surveyId, activityId);

        int surveyActivityId = (int) this.surveyActivityDao.insert(record);

        List<SurveyResponseActivityRecord> actualRecords = this.surveyActivityDao.getSurveyByActivityId();

        assertThat(actualRecords.size()).isGreaterThan(0);

        assertThat(actualRecords.get(0).getSurveyResponseId())
                .isEqualTo(record.getSurveyResponseId());

        assertThat(actualRecords.get(0).getActivityRecordId())
                .isEqualTo(record.getActivityRecordId());
    }

    @Test
    public void insertingASurveyIdWhichDoesNotExist_ShouldThrowAConstraintException() {
        // ToDo activity should exist
        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(112233, 332211);

        Exception exception = new Exception();

        try {
            this.surveyActivityDao.insert(record);
            fail("Should have thrown an SQLIntegrityConstraintViolationException exception");
        } catch(Exception e) {
            exception = e;
        }

        assertWithMessage("Inserting a survey Id of a survey response that doesn't exist should violate constraints")
                .that(exception)
                .isInstanceOf(SQLIntegrityConstraintViolationException.class);
    }

    @Test
    public void insertingAnActionIdWhichDoesNotExist_ShouldThrowAConstraintException() {
        // ToDo create a survey so that it does exist
        // ToDo need to implement the survey tests first
        SurveyResponseActivityRecord record = new SurveyResponseActivityRecord(112233, 332211);

        Exception exception = new Exception();

        try {
            this.surveyActivityDao.insert(record);
            fail("Should have thrown an SQLIntegrityConstraintViolationException exception");
        } catch(Exception e) {
            exception = e;
        }

        assertWithMessage("Inserting an activity Id of a activity record that doesn't exist should violate constraints")
                .that(exception)
                .isInstanceOf(SQLIntegrityConstraintViolationException.class);
    }
}
