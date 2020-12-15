package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SurveyResponseElementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SurveyResponseElement surveyResponseElements);

    @Query("SELECT * FROM survey_response_element")
    List<SurveyResponseElement> getSurveyResponseElementBySurveyResponseElementId();

    // ToDo make id = survey_id =
    @Query("SELECT * FROM survey_response_element WHERE id = :surveyId")
    List<SurveyResponseElement> getBySurveyResponseElementBySurveyResponseId(int surveyId);
}
