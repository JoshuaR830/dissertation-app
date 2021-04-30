package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseQuestionHelper.ACTIVITY_OF_TYPE_VALUE;

/**
 * Provides methods for accessing the wellbeing_questions table
 */
@Dao
public interface WellbeingQuestionDao {
    /**
     * Insert a new question into the table
     *
     * @param wellbeingQuestion The question to insert
     * @return An id for the question
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(WellbeingQuestion wellbeingQuestion);

    /**
     * Get the specific sub-activity question by id
     *
     * @param id The id of the question to retrieve
     * @return The question record
     */
    @Query("SELECT * FROM wellbeing_questions WHERE wellbeing_question_id == :id")
    WellbeingQuestion getQuestionById(long id);

    /**
     * Get questions by activity type.
     * Each activity should have a type, this should get appropriate sub-activities for the activity.
     *
     * @param activityType The type of the activity
     * @return A list of questions matching the type
     */
    @Query("SELECT * FROM wellbeing_questions WHERE activity_type == :activityType")
    List<WellbeingQuestion> getQuestionsByActivityType(String activityType);

    /**
     * Get the observable collated ways to wellbeing achieved between a start and end time updated in real time.
     * Includes both activities and sub-activities.
     *
     * @param startTime The start time for the query
     * @param endTime The end time for the query
     * @return The collated values for each wellbeing type
     */
    @Query(
        "SELECT wayToWellbeing, SUM(num) AS value FROM ( " +
            "SELECT wellbeing_questions.way_to_wellbeing AS wayToWellbeing, SUM(wellbeing_questions.weighting) AS num FROM wellbeing_questions " +
                "INNER JOIN wellbeing_records ON wellbeing_records.question_id == wellbeing_questions.wellbeing_question_id " +
                "WHERE  user_input == 1 AND wellbeing_records.time BETWEEN :startTime AND :endTime " +
                "GROUP BY wellbeing_questions.way_to_wellbeing " +
            "UNION ALL " +
            "SELECT activity_records.way_to_wellbeing AS wayToWellbeing, SUM("+ ACTIVITY_OF_TYPE_VALUE +") AS num FROM survey_activity " +
                "INNER JOIN activity_records ON survey_activity.activity_record_id == activity_records.id " +
                "INNER JOIN survey_response ON survey_activity.survey_response_id == survey_response.id " +
                "WHERE survey_response.timestamp BETWEEN :startTime AND :endTime " +
                "GROUP BY activity_records.way_to_wellbeing )" +
        "GROUP BY wayToWellbeing"
    )
    LiveData<List<WellbeingGraphItem>> getWaysToWellbeingBetweenTimes(long startTime, long endTime);

    /**
     * Get a snapshot of the collated ways to wellbeing achieved between a start and end time.
     * Includes both activities and sub-activities.
     *
     * @param startTime The start time for the query
     * @param endTime The end time for the query
     * @return The collated values for each wellbeing type
     */
    @Query(
        "SELECT wayToWellbeing, SUM(num) AS value FROM ( " +
            "SELECT wellbeing_questions.way_to_wellbeing AS wayToWellbeing, SUM(wellbeing_questions.weighting) AS num FROM wellbeing_questions " +
                "INNER JOIN wellbeing_records ON wellbeing_records.question_id == wellbeing_questions.wellbeing_question_id " +
                "WHERE  user_input == 1 AND wellbeing_records.time BETWEEN :startTime AND :endTime " +
                "GROUP BY wellbeing_questions.way_to_wellbeing " +
            "UNION ALL " +
            "SELECT activity_records.way_to_wellbeing AS wayToWellbeing, SUM(" + ACTIVITY_OF_TYPE_VALUE + ") AS num FROM survey_activity " +
                "INNER JOIN activity_records ON survey_activity.activity_record_id == activity_records.id " +
                "INNER JOIN survey_response ON survey_activity.survey_response_id == survey_response.id " +
                "WHERE survey_response.timestamp BETWEEN :startTime AND :endTime " +
                "GROUP BY activity_records.way_to_wellbeing ) " +
            "GROUP BY wayToWellbeing"
    )
    List<WellbeingGraphItem> getWaysToWellbeingBetweenTimesNotLive(long startTime, long endTime);

    /**
     * Delete the selected wellbeing question
     *
     * @param wellbeingQuestion The question to delete
     */
    @Delete
    void delete(WellbeingQuestion wellbeingQuestion);

    /**
     * Update a wellbeing question with new values
     *
     * @param id The id of the question to update
     * @param question The question that should be asked in the sub-activity checkbox
     * @param positiveMessage The positive message that should be displayed as a positivity boost
     * @param negativeMessage The negative message that should be displayed as a suggestion
     */
    @Query("UPDATE wellbeing_questions SET question = :question, positive_message = :positiveMessage , negative_message = :negativeMessage WHERE wellbeing_questions.wellbeing_question_id = :id")
    void updateQuestion(long id, String question, String positiveMessage, String negativeMessage);
}
