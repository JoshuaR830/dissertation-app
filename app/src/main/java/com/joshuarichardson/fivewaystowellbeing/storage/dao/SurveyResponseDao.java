package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SurveyResponseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SurveyResponse surveyResponse);

    @Query("SELECT * FROM survey_response WHERE id = :surveyId")
    SurveyResponse getSurveyResponseById(long surveyId);

    @Query("SELECT * FROM survey_response")
    LiveData<List<SurveyResponse>> getAllSurveyResponses();

    @Query("SELECT * FROM survey_response WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp")
    LiveData<List<SurveyResponse>> getSurveyResponsesByTimestampRange(long startTimestamp, long endTimestamp);

    @Query("SELECT COUNT(way_to_wellbeing) FROM survey_response WHERE way_to_wellbeing = :wayToWellbeing")
    int getInsights(String wayToWellbeing);

    // ToDo - this could be made daily
    @Query("SELECT COUNT(way_to_wellbeing) FROM survey_response WHERE way_to_wellbeing = :wayToWellbeing")
    LiveData<Integer> getLiveInsights(String wayToWellbeing);

    @Query("SELECT * FROM survey_response ORDER BY timestamp DESC")
    List<SurveyResponse> getHistoryPageData();

    // ToDo Will need to add a delete
}
