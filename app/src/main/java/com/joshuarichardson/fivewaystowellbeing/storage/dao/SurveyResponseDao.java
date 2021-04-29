package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Provides methods for accessing the survey_response table
 */
@Dao
public interface SurveyResponseDao {
    /**
     * Insert a new survey item
     *
     * @param surveyResponse The survey item to insert
     * @return The id for the newly inserted survey
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SurveyResponse surveyResponse);

    /**
     * Get a specific survey
     *
     * @param surveyId The id of the survey to retrieve
     * @return The requested survey record
     */
    @Query("SELECT * FROM survey_response WHERE id = :surveyId")
    SurveyResponse getSurveyResponseById(long surveyId);

    @Query("SELECT * FROM survey_response WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp ORDER BY timestamp")
    LiveData<List<SurveyResponse>> getSurveyResponsesByTimestampRange(long startTimestamp, long endTimestamp);

    /**
     * Get an fixed list of the surveys between two timestamps (inclusive)
     *
     * @param startTimestamp The time of the first survey in milliseconds
     * @param endTimestamp The time of the last survey in milliseconds
     * @return An ordered list of surveys between the specified times
     */
    @Query("SELECT * FROM survey_response WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp ORDER BY timestamp")
    List<SurveyResponse> getSurveyResponsesByTimestampRangeNotLive(long startTimestamp, long endTimestamp);

    /**
     * Get the number of days with any logged activities between times (inclusive)
     *
     * @param startTime The start time in milliseconds
     * @param endTime The end time in milliseconds
     * @return The number of days that have wellbeing data
     */
    @Query("SELECT COUNT(*) FROM (SELECT DISTINCT survey_response.id FROM survey_response INNER JOIN survey_activity ON survey_response.id = survey_activity.survey_response_id WHERE timestamp BETWEEN :startTime AND :endTime)")
    int getNumDaysWithWaysToWellbeingByDate(long startTime, long endTime);

    /**
     * Get only surveys that have activities logged
     *
     * @return An observable list of all non-empty historical surveys
     */
    @Query("SELECT DISTINCT survey_response.id, survey_response.description, survey_response.timestamp, survey_response.title, survey_response.way_to_wellbeing " +
            "FROM survey_response " +
            "INNER JOIN survey_activity ON survey_response.id = survey_activity.survey_response_id " +
            "ORDER BY timestamp DESC")
    LiveData<List<SurveyResponse>> getNonEmptyHistoryPageData();

    /**
     * Get only surveys that have no data
     *
     * @return An observable list of all missed days
     */
    @Query("SELECT * FROM survey_response WHERE id not in (SELECT DISTINCT survey_response.id " +
            "FROM survey_response " +
            "INNER JOIN survey_activity ON survey_response.id = survey_activity.survey_response_id) " +
            "ORDER BY timestamp DESC")
    LiveData<List<SurveyResponse>> getEmptyHistoryPageData();

    /**
     * Test query - get all of the surveys
     * @return A list of every survey
     */
    @Query("SELECT * FROM survey_response")
    LiveData<List<SurveyResponse>> getAllSurveyResponses();
}
