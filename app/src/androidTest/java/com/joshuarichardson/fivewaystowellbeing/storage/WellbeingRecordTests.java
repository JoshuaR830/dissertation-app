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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class WellbeingRecordTests {
    private WellbeingDatabase wellbeingDb;
    private WellbeingRecordDao wellbeingRecordDao;
    private WellbeingQuestionDao questionsDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    private long recordId1;
    private long recordId2;
    private long recordId3;
    private long recordId4;
    private long recordId5;
    private long surveyActivityId1;
    private long surveyActivityId2;
    private long surveyActivityId3;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.wellbeingRecordDao = wellbeingDb.wellbeingRecordDao();
        this.questionsDao = wellbeingDb.wellbeingQuestionDao();
        SurveyResponseActivityRecordDao surveyActivityDao = wellbeingDb.surveyResponseActivityRecordDao();
        SurveyResponseDao surveyResponseDao = wellbeingDb.surveyResponseDao();
        ActivityRecordDao activityRecordDao = wellbeingDb.activityRecordDao();

        long surveyId = surveyResponseDao.insert(new SurveyResponse(1607960245, "Be active", "title", "description"));
        long activityId = activityRecordDao.insert(new ActivityRecord("Running", 1200, 1607960240, "Sport", "UNASSIGNED"));

        this.surveyActivityId1 = surveyActivityDao.insert(new SurveyResponseActivityRecord(surveyId, activityId, 1, "note 1",  1612427791, 1612427795));
        this.surveyActivityId2 = surveyActivityDao.insert(new SurveyResponseActivityRecord(surveyId, activityId, 2, "note 2",  1712427792, 1712427796));
        this.surveyActivityId3 = surveyActivityDao.insert(new SurveyResponseActivityRecord(surveyId, activityId, 1, "note 3",  1812427793, 1812427797));

        this.questionsDao.insert(new WellbeingQuestion(123, "Question", "Positive", "Negative", WaysToWellbeing.GIVE.toString(), 1, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()));
        this.questionsDao.insert(new WellbeingQuestion(234, "Question", "Positive", "Negative", WaysToWellbeing.GIVE.toString(), 1, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString() ));
        this.questionsDao.insert(new WellbeingQuestion(456, "Question", "Positive", "Negative", WaysToWellbeing.GIVE.toString(), 1, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString() ));

        this.recordId1 = this.wellbeingRecordDao.insert(new WellbeingRecord(true, 5494589, this.surveyActivityId1, 3, 123));
        this.recordId2 = this.wellbeingRecordDao.insert(new WellbeingRecord(true, 6494589, this.surveyActivityId1, 1, 234));
        this.recordId3 = this.wellbeingRecordDao.insert(new WellbeingRecord(true, 7494589, this.surveyActivityId1, 2, 456));
        this.recordId4 = this.wellbeingRecordDao.insert(new WellbeingRecord(true, 8494589, this.surveyActivityId2, 1, 456));
        this.recordId5 = this.wellbeingRecordDao.insert(new WellbeingRecord(true, 9494589, this.surveyActivityId3, 1, 456));
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void gettingWellbeingRecordById_ShouldReturnTheCorrectWellbeingRecord() {
        WellbeingRecord wellbeingRecord = this.wellbeingRecordDao.getWellbeingRecordById(this.recordId5);

        assertThat(wellbeingRecord.getUserInput())
            .isEqualTo(true);

        assertThat(wellbeingRecord.getTime())
            .isEqualTo(9494589);

        assertThat(wellbeingRecord.getSurveyActivityId())
            .isEqualTo(this.surveyActivityId3);

        assertThat(wellbeingRecord.getSequenceNumber())
            .isEqualTo(1);

        assertThat(wellbeingRecord.getQuestionId())
            .isEqualTo(456);
    }

    @Test
    public void gettingWellbeingRecordsByActivitySurveyIdWhereMultipleRecordsExist_ShouldReturnMultipleWellbeingRecordsInSequenceOrder() {
        List<WellbeingRecord> records = this.wellbeingRecordDao.getWellbeingRecordsBySurveyActivityId(this.surveyActivityId1);

        assertThat(records.size())
            .isEqualTo(3);

        assertThat(records.get(0).getSequenceNumber())
            .isEqualTo(1);

        assertThat(records.get(1).getSequenceNumber())
            .isEqualTo(2);

        assertThat(records.get(2).getSequenceNumber())
            .isEqualTo(3);
    }

    @Test
    public void gettingWellbeingRecordsByActivitySurveyIdWhereOnlyOneRecordExists_ShouldReturnOneWellbeingRecord() {
        List<WellbeingRecord> records = this.wellbeingRecordDao.getWellbeingRecordsBySurveyActivityId(this.surveyActivityId2);
        assertThat(records.size())
            .isEqualTo(1);

        assertThat(records.get(0).getTime())
            .isEqualTo(8494589);
    }

    @Test
    public void gettingWellbeingRecordByNonExistentActivitySurveyId_ShouldReturnNoWellbeingRecords() {
        List<WellbeingRecord> records = this.wellbeingRecordDao.getWellbeingRecordsBySurveyActivityId(111);

        assertThat(records.size())
            .isEqualTo(0);
    }

    @Test
    public void onDelete_TheItemShouldBeDeleted() {
        WellbeingRecord record = new WellbeingRecord(true, 5494589, this.surveyActivityId1, 3, 123);
        record.setId(this.recordId1);
        this.wellbeingRecordDao.delete(record);
        WellbeingRecord deletedItem = this.wellbeingRecordDao.getWellbeingRecordById(this.recordId1);

        assertThat(deletedItem).isNull();
    }

    @Test
    public void onDelete_TheQuestionShouldBeDeletedIfThereAreMultipleRecords() {
        this.questionsDao.delete(new WellbeingQuestion(456, "Question", "Positive", "Negative", WaysToWellbeing.GIVE.toString(), 1, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString() ));
        WellbeingRecord record = this.wellbeingRecordDao.getWellbeingRecordById(this.recordId1);
        assertThat(record).isNotNull();
        record = this.wellbeingRecordDao.getWellbeingRecordById(this.recordId2);
        assertThat(record).isNotNull();
        record = this.wellbeingRecordDao.getWellbeingRecordById(this.recordId3);
        assertThat(record).isNull();
        record = this.wellbeingRecordDao.getWellbeingRecordById(this.recordId4);
        assertThat(record).isNull();
        record = this.wellbeingRecordDao.getWellbeingRecordById(this.recordId5);
        assertThat(record).isNull();
    }

    @Test
    public void onDelete_TheWellbeingRecordsShouldBeDeletedIfThereIsOneReference() {
        this.questionsDao.delete(new WellbeingQuestion(234, "Question", "Positive", "Negative", WaysToWellbeing.GIVE.toString(), 1, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()));

        WellbeingRecord record = this.wellbeingRecordDao.getWellbeingRecordById(this.recordId2);
        assertThat(record).isNull();
    }
}
