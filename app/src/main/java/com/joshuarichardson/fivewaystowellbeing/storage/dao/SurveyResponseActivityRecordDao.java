package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SurveyResponseActivityRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SurveyResponseActivityRecord surveyResponseActivityRecord);

    @Query("SELECT * FROM survey_activity")
    List<SurveyResponseActivityRecord> getActivitiesBySurveyId();

    @Query("SELECT * FROM survey_activity")
    List<SurveyResponseActivityRecord> getSurveyByActivityId();

    // Need a delete method
}
