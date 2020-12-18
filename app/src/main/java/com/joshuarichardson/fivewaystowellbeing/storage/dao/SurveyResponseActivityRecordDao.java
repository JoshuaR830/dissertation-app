package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SurveyResponseActivityRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SurveyResponseActivityRecord surveyResponseActivityRecord);

    // ToDo should be a join to get all of them for an activity or a survey

    @Query("SELECT * FROM survey_activity")
    LiveData<List<SurveyResponseActivityRecord>> getActivitiesBySurveyId();

    @Query("SELECT * FROM survey_activity")
    LiveData<List<SurveyResponseActivityRecord>> getSurveyByActivityId();

    // ToDo Need a delete method
}
