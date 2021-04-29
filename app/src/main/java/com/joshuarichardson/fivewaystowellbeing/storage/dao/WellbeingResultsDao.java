package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Provides methods for accessing the wellbeing_results table
 */
@Dao
public interface WellbeingResultsDao {

    /**
     * Insert a new wellbeing result that contains a snapshot of the percentage of ways to wellbeing achieved
     *
     * @param result The result to insert
     * @return The id of the inserted result
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(WellbeingResult result);

    /**
     * Update the wellbeing result to have the correct wellbeing values
     *
     * @param surveyId The id of the survey to associate the results with
     * @param connectValue The percentage of connect achieved
     * @param beActiveValue The percentage of connect be active achieved
     * @param keepLearningValue The percentage of keep learning achieved
     * @param takeNoticeValue The percentage of take notice achieved
     * @param giveValue The percentage of give achieved
     */
    @Query("UPDATE wellbeing_result SET connect = :connectValue, be_active = :beActiveValue, keep_learning = :keepLearningValue, take_notice = :takeNoticeValue, give = :giveValue WHERE id = :surveyId")
    void updateWaysToWellbeing(long surveyId, int connectValue, int beActiveValue, int keepLearningValue, int takeNoticeValue, int giveValue);

    /**
     * Get the ways to wellbeing achieved for a specific survey
     *
     * @param surveyId the id of the survey
     * @return The ways to wellbeing percentages
     */
    @Query("SELECT * FROM wellbeing_result WHERE id = :surveyId")
    WellbeingResult getResultsBySurveyId(long surveyId);

    /**
     * Get wellbeing results between two times (inclusive)
     *
     * @param startTime The start time in milliseconds
     * @param endTime The end time in milliseconds
     * @return The list of wellbeing results by survey in those time ranges
     */
    @Query("SELECT * FROM wellbeing_result WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    List<WellbeingResult> getResultsByTimestampRange(long startTime, long endTime);
}
