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

    @Query("SELECT * FROM survey_response WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp ORDER BY timestamp")
    LiveData<List<SurveyResponse>> getSurveyResponsesByTimestampRange(long startTimestamp, long endTimestamp);

    @Query("SELECT * FROM survey_response WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp ORDER BY timestamp")
    List<SurveyResponse> getSurveyResponsesByTimestampRangeNotLive(long startTimestamp, long endTimestamp);

    @Query("SELECT COUNT(way_to_wellbeing) FROM survey_response WHERE way_to_wellbeing = :wayToWellbeing")
    int getInsights(String wayToWellbeing);

    // ToDo - this could be made daily
    @Query("SELECT COUNT(way_to_wellbeing) FROM survey_response WHERE way_to_wellbeing = :wayToWellbeing")
    LiveData<Integer> getLiveInsights(String wayToWellbeing);

    @Query("SELECT * FROM survey_response ORDER BY timestamp DESC")
    List<SurveyResponse> getHistoryPageData();

    @Query("SELECT COUNT(*) FROM (SELECT DISTINCT survey_response.id FROM survey_response INNER JOIN survey_activity ON survey_response.id = survey_activity.survey_response_id WHERE timestamp BETWEEN :startTime AND :endTime)")
    int getNumDaysWithWaysToWellbeingByDate(long startTime, long endTime);

    // Select only surveys that have data in them
    @Query("SELECT DISTINCT survey_response.id, survey_response.description, survey_response.timestamp, survey_response.title, survey_response.way_to_wellbeing " +
            "FROM survey_response " +
            "INNER JOIN survey_activity ON survey_response.id = survey_activity.survey_response_id " +
            "ORDER BY timestamp DESC")
    LiveData<List<SurveyResponse>> getNonEmptyHistoryPageData();

    // Find filled in items then exclude those from the rest of the items
    @Query("SELECT * FROM survey_response WHERE id not in (SELECT DISTINCT survey_response.id " +
            "FROM survey_response " +
            "INNER JOIN survey_activity ON survey_response.id = survey_activity.survey_response_id) " +
            "ORDER BY timestamp DESC")
    LiveData<List<SurveyResponse>> getEmptyHistoryPageData();

    // ToDo Will need to add a delete
}
