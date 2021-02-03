package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;

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

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.wellbeingRecordDao = wellbeingDb.wellbeingRecordDao();

        this.wellbeingRecordDao.insert(new WellbeingRecord(true, 5494589, 123, 3, 123));
        this.wellbeingRecordDao.insert(new WellbeingRecord(true, 6494589, 123, 1, 234));
        this.wellbeingRecordDao.insert(new WellbeingRecord(true, 7494589, 123, 2, 456));
        this.wellbeingRecordDao.insert(new WellbeingRecord(true, 8494589, 234, 1, 456));
        this.wellbeingRecordDao.insert(new WellbeingRecord(true, 9494589, 345, 1, 456));
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void insertingWellbeingRecords_ThenGettingWellbeingRecordById_ShouldReturnTheCorrectWellbeingRecord() {
        WellbeingRecord wellbeingRecord = this.wellbeingRecordDao.getWellbeingRecordById(345);

        assertThat(wellbeingRecord.getUserInput())
            .isEqualTo(true);

        assertThat(wellbeingRecord.getTime())
            .isEqualTo(9494589);

        assertThat(wellbeingRecord.getSurveyActivityId())
            .isEqualTo(345);

        assertThat(wellbeingRecord.getSequenceNumber())
            .isEqualTo(1);

        assertThat(wellbeingRecord.getQuestionId())
            .isEqualTo(456);
    }

    @Test
    public void insertingWellbeingRecords_ThenGettingWellbeingRecordsByActivitySurveyIdWhereMultipleRecordsExist_ShouldReturnMultipleWellbeingRecordsInSequenceOrder() {
        List<WellbeingRecord> records = this.wellbeingRecordDao.getWellbeingRecordsByActivitySurveyId(123);

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
    public void insertingWellbeingRecords_ThenGettingWellbeingRecordsByActivitySurveyIdWhereOnlyOneRecordExists_ShouldReturnOneWellbeingRecord() {
        List<WellbeingRecord> records = this.wellbeingRecordDao.getWellbeingRecordsByActivitySurveyId(234);
        assertThat(records.size())
                .isEqualTo(1);

        assertThat(records.get(0).getTime())
                .isEqualTo(8494589);
    }

    @Test
    public void insertingWellbeingRecords_ThenGettingWellbeingRecordByyNonExistentActivitySurveyId_ShouldReturnNoWellbeingRecords() {
        List<WellbeingRecord> records = this.wellbeingRecordDao.getWellbeingRecordsByActivitySurveyId(111);

        assertThat(records.size())
            .isEqualTo(0);
    }
}
