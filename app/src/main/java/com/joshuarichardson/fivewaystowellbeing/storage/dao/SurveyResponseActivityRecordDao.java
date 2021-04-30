package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.ActivityStats;
import com.joshuarichardson.fivewaystowellbeing.storage.SurveyCountItem;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Provides methods for accessing the survey_activity table
 */
@Dao
public interface SurveyResponseActivityRecordDao {
    /**
     * Add an instance of an activity to a survey
     *
     * @param surveyResponseActivityRecord A new instance of an activity to be added to a survey
     * @return The id of the survey activity record
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SurveyResponseActivityRecord surveyResponseActivityRecord);

    /**
     * Update the survey note for the activity instance
     *
     * @param surveyActivityId The id of the activity instance to update
     * @param noteText The text to set the note to
     */
    @Query("UPDATE survey_activity SET note = :noteText WHERE survey_activity_id = :surveyActivityId")
    void updateNote(long surveyActivityId, String noteText);

    /**
     * Update the start time associated with an activity instance
     *
     * @param surveyActivityId The id of the activity instance to update
     * @param startTime The time in milliseconds to set it to
     */
    @Query("UPDATE survey_activity SET start_time = :startTime WHERE survey_activity_id = :surveyActivityId")
    void updateStartTime(long surveyActivityId, long startTime);

    /**
     * Update the end time associated with an activity instance
     *
     * @param surveyActivityId The id of the activity instance to update
     * @param endTime The time in milliseconds to set it to
     */
    @Query("UPDATE survey_activity SET end_time = :endTime WHERE survey_activity_id = :surveyActivityId")
    void updateEndTime(long surveyActivityId, long endTime);

    /**
     * Update the emotion associated with an activity instance
     *
     * @param surveyActivityId The id of the activity instance to update
     * @param emotion A number 1-5 representing the emotion
     */
    @Query("UPDATE survey_activity SET emotion = :emotion WHERE survey_activity_id = :surveyActivityId")
    void updateEmotion(long surveyActivityId, int emotion);

    /**
     * Set an activity instance as done to hide the expandable menu
     *
     * @param surveyActivityId The id of the activity instance to update
     * @param isDone The completion status
     */
    @Query("UPDATE survey_activity SET is_done = :isDone WHERE survey_activity_id = :surveyActivityId")
    void updateIsDone(long surveyActivityId, boolean isDone);

    /**
     * Count the number of emotion items and the cumulative emotion score
     *
     * @param surveyId The id of the survey to count the emotions for
     * @return An observable collated emotion score
     */
    @Query("SELECT COUNT(*) AS emotionCount, SUM(emotion) AS totalValue FROM survey_activity WHERE survey_response_id = :surveyId AND emotion != 0")
    LiveData<SurveyCountItem> getEmotions(long surveyId);

    /**
     * Get the number of items in a survey
     *
     * @param surveyId The survey to find out the number of activities for
     * @return The number of items
     */
    @Query("SELECT COUNT(*) AS activityCount FROM survey_activity WHERE survey_response_id = :surveyId")
    int getItemCount(long surveyId);

    /**
     * Delete an instance of an activity
     *
     * @param activitySurveyId The id of the activity instance to delete
     */
    @Query("DELETE FROM survey_activity WHERE survey_activity_id = :activitySurveyId")
    void deleteById(long activitySurveyId);

    /**
     * Retrieve the number of times an activity has been completed between time periods (inclusive) and sort them high to low
     *
     * @param startTime The start time in milliseconds
     * @param endTime The end time in milliseconds
     * @return A list containing the number of each activity achieved
     */
    @Query("SELECT activity_records.id AS activityId, count(*) AS count FROM activity_records " +
        "JOIN survey_activity ON survey_activity.activity_record_id = activity_records.id " +
        "INNER JOIN survey_response ON survey_activity.survey_response_id = survey_response.id " +
        "WHERE activity_records.is_hidden = 0 AND survey_response.timestamp BETWEEN :startTime AND :endTime " +
        "GROUP BY activity_records.id " +
        "ORDER BY count(*) DESC")
    List<ActivityStats> getActivityFrequencyBetweenTimes(long startTime, long endTime);

    /**
     * Retrieve the number of times an activity attributing to a specific way to wellbeing has been completed between time periods (inclusive) and sort them high to low
     *
     * @param startTime The start time in milliseconds
     * @param endTime The end time in milliseconds
     * @param wayToWellbeing The way to wellbeing that the activities should be
     * @return A list containing the number of each activity achieved
     */
    @Query("SELECT activity_records.id AS activityId, count(*) AS count FROM activity_records " +
        "JOIN survey_activity ON survey_activity.activity_record_id = activity_records.id " +
        "INNER JOIN survey_response ON survey_activity.survey_response_id = survey_response.id " +
        "WHERE activity_records.is_hidden = 0 AND survey_response.timestamp BETWEEN :startTime AND :endTime AND activity_records.way_to_wellbeing = :wayToWellbeing " +
        "GROUP BY activity_records.id " +
        "ORDER BY count(*) DESC")
    List<ActivityStats> getActivityFrequencyByWellbeingTypeBetweenTimes(long startTime, long endTime, String wayToWellbeing);


    /**
     * Test query - get survey activity for a given survey id
     *
     * @param surveyResponseId The survey id to get the activities for
     * @return A list of survey activities
     */
    @Query("SELECT * FROM survey_activity WHERE survey_response_id = :surveyResponseId ORDER BY sequence_number")
    List<SurveyResponseActivityRecord> getActivityRecordDetailsForSurvey(long surveyResponseId);

    /**
     * Test query - get activities associated with a specific survey
     *
     * @param surveyId The survey id to get the activities for
     * @return A list of activities
     */
    @Query("SELECT * FROM activity_records " +
        "INNER JOIN survey_activity ON activity_records.id=survey_activity.activity_record_id " +
        "WHERE survey_activity.survey_response_id = :surveyId ORDER BY sequence_number")
    List<ActivityRecord> getActivitiesBySurveyId(long surveyId);

    /**
     * Test Query - Get survey by activity id
     *
     * @param activityId The activity id to retrieve the surveys
     * @return An observable list of surveys that have activity instances
     */
    @Query("SELECT * FROM survey_response " +
        "INNER JOIN survey_activity ON survey_response.id = survey_activity.survey_response_id " +
        "WHERE survey_activity.activity_record_id = :activityId")
    LiveData<List<SurveyResponse>> getSurveyByActivityId(long activityId);

    /**
     * Test query - get the survey activity activity instance using its id
     *
     * @param surveyActivityId The id of the record
     * @return The instance of the activity
     */
    @Query("SELECT * FROM survey_activity WHERE survey_activity_id = :surveyActivityId")
    SurveyResponseActivityRecord getSurveyActivityById(long surveyActivityId);
}
