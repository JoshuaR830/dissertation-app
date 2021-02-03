package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface WellbeingRecordDao {
    @Insert
    long insert(WellbeingRecord wellbeingRecord);

    @Query("SELECT * FROM wellbeing_records WHERE survey_response_activity_record_id = :surveyResponseActivityRecordId")
    List<WellbeingRecord> getWellbeingRecordsByActivitySurveyId(int surveyResponseActivityRecordId);

    @Query("SELECT * FROM wellbeing_records WHERE id = :id")
    WellbeingRecord getWellbeingRecordById(int id);
}
