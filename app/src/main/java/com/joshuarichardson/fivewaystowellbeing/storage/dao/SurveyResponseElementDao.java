package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SurveyResponseElementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SurveyResponseElement surveyResponseElements);

    @Query("SELECT * FROM survey_response_element WHERE id = :surveyElementId")
    LiveData<List<SurveyResponseElement>> getSurveyResponseElementBySurveyResponseElementId(int surveyElementId);

    @Query("SELECT * FROM survey_response_element WHERE survey_id = :surveyId")
    LiveData<List<SurveyResponseElement>> getSurveyResponseElementBySurveyResponseId(int surveyId);
}
