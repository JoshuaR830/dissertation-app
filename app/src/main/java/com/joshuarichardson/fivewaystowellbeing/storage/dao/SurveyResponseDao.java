package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SurveyResponseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SurveyResponse surveyResponse);

    @Query("SELECT * FROM survey_response WHERE survey_response_id = :surveyId")
    List<SurveyResponse> getSurveyResponseById(int surveyId);

    // ToDo Will need to add a delete
}
