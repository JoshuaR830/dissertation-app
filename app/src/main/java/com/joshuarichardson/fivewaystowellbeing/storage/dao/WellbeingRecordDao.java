package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.LimitedRawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Provides methods for accessing the wellbeing_records table
 */
@Dao
public interface WellbeingRecordDao {
    /**
     * Insert a new wellbeing record
     *
     * @param wellbeingRecord The wellbeing record to insert
     * @return The id of the inserted wellbeing record
     */
    @Insert
    long insert(WellbeingRecord wellbeingRecord);

    /**
     * Get a specific wellbeing record
     *
     * @param id The id of the wellbeing record to retrieve
     * @return The retrieved wellbeing record
     */
    @Query("SELECT * FROM wellbeing_records WHERE wellbeing_record_id = :id")
    WellbeingRecord getWellbeingRecordById(long id);

    /**
     * All activities that are related to the specified survey will be returned allowing a daily wellbeing log to be constructed.
     * Get all parts of all activities including associated survey data and question data
     *
     * @param surveyId The id of the survey
     * @return All of the data required to display a daily wellbeing log
     */
    @Query("SELECT survey_response.timestamp AS date, survey_response.description AS surveyNote, survey_activity.note AS activityNote, activity_records.name AS activityName, survey_activity.survey_activity_id AS surveyActivityId, survey_activity.emotion AS emotion, survey_activity.is_done AS isDone, wellbeing_questions.question AS question, wellbeing_records.wellbeing_record_id AS wellbeingRecordId, wellbeing_records.user_input AS userInput, activity_records.type AS activityType, activity_records.way_to_wellbeing AS wayToWellbeing, survey_activity.start_time AS startTime, survey_activity.end_time AS endTime " +
        "FROM wellbeing_records " +
        "INNER JOIN wellbeing_questions ON wellbeing_records.question_id == wellbeing_questions.wellbeing_question_id " + // This is for getting the questions
        "INNER JOIN survey_activity ON survey_activity.survey_activity_id == wellbeing_records.survey_response_activity_record_id " + // This is for getting the wellbeing record associated with a survey activity pair
        "INNER JOIN activity_records ON activity_records.id == survey_activity.activity_record_id " + // This is for getting the activity
        "INNER JOIN survey_response ON survey_activity.survey_response_id == survey_response.id " + // This is for getting the survey
        "WHERE survey_response.id == :surveyId ORDER BY survey_activity.sequence_number ASC, wellbeing_records.sequence_number ASC")
    List<RawSurveyData> getDataBySurvey(long surveyId);

    /**
     * To support old activities, can return a limited set of data that can be used to display activities in the daily wellbeing log
     *
     * @param surveyId The id of the old survey
     * @return A limited set of data for compatibility with old activities
     */
    @Query("SELECT survey_response.timestamp AS date, survey_response.description AS surveyNote, activity_records.name AS activityName, activity_records.type AS activityType " +
        "FROM survey_activity " +
        "INNER JOIN activity_records ON activity_records.id == survey_activity.activity_record_id " + // This is for getting the activity
        "INNER JOIN survey_response ON survey_activity.survey_response_id == survey_response.id " + // This is for getting the survey
        "WHERE survey_response.id == :surveyId ORDER BY activity_records.timestamp")
    List<LimitedRawSurveyData> getLimitedDataBySurvey(long surveyId);

    /**
     * Update the checked status of the wellbeing record
     *
     * @param wellbeingRecordId The id of the wellbeing record to check
     * @param isChecked The checkbox status
     */
    @Query("UPDATE wellbeing_records SET user_input = :isChecked WHERE wellbeing_record_id = :wellbeingRecordId")
    void checkItem(long wellbeingRecordId, boolean isChecked);

    /**
     * Delete a wellbeing record that is no longer required
     *
     * @param wellbeingRecord The wellbeing record to delete
     */
    @Delete
    void delete(WellbeingRecord wellbeingRecord);

    /**
     * Get all of the checked wellbeing records in a given time range (inclusive)
     *
     * @param startTime The start time in milliseconds
     * @param endTime The end time in milliseconds
     * @param wayToWellbeing The way to wellbeing to check
     * @return An observable list of checked sub-activities
     */
    @Query("SELECT * FROM wellbeing_records INNER JOIN wellbeing_questions ON wellbeing_records.question_id = wellbeing_questions.wellbeing_question_id WHERE wellbeing_questions.way_to_wellbeing = :wayToWellbeing AND wellbeing_records.user_input = 1 AND wellbeing_records.time BETWEEN :startTime AND :endTime GROUP BY wellbeing_questions.wellbeing_question_id")
    LiveData<List<WellbeingQuestion>> getTrueWellbeingRecordsByTimestampRangeAndWayToWellbeingType(long startTime, long endTime, String wayToWellbeing);

    /**
     * Get all of the unchecked wellbeing records in a given time range (inclusive)
     *
     * @param startTime The start time in milliseconds
     * @param endTime The end time in milliseconds
     * @param wayToWellbeing The way to wellbeing to check
     * @return An observable list of unchecked sub-activities
     */
    @Query("SELECT * FROM wellbeing_records INNER JOIN wellbeing_questions ON wellbeing_records.question_id = wellbeing_questions.wellbeing_question_id WHERE wellbeing_questions.way_to_wellbeing = :wayToWellbeing AND wellbeing_records.user_input = 0 AND wellbeing_records.time BETWEEN :startTime AND :endTime GROUP BY wellbeing_questions.wellbeing_question_id")
    LiveData<List<WellbeingQuestion>> getFalseWellbeingRecordsByTimestampRangeAndWayToWellbeingType(long startTime, long endTime, String wayToWellbeing);

    /**
     * Test query - get all wellbeing records associated with an instance of an activity
     *
     * @param surveyActivityId The survey activity id of the activity instance to get the wellbeing records for
     * @return All wellbeing records associated with the activity instance
     */
    @Query("SELECT * FROM wellbeing_records WHERE survey_response_activity_record_id = :surveyActivityId ORDER BY sequence_number ASC")
    List<WellbeingRecord> getWellbeingRecordsBySurveyActivityId(long surveyActivityId);

}
