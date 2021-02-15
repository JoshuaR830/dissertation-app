package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import static com.joshuarichardson.fivewaystowellbeing.storage.DatabaseQuestionHelper.ACTIVITY_OF_TYPE_VALUE;

@Dao
public interface WellbeingQuestionDao {
    @Insert
    long insert(WellbeingQuestion wellbeingQuestion);

    @Query("SELECT * FROM wellbeing_questions WHERE wellbeing_question_id == :id")
    WellbeingQuestion getQuestionById(long id);

    @Query("SELECT * FROM wellbeing_questions WHERE activity_type == :activityType")
    List<WellbeingQuestion> getQuestionsByActivityType(String activityType);

    @Query("SELECT negative_message FROM wellbeing_questions WHERE wellbeing_questions.wellbeing_question_id == :id")
    String getNegativeResponseById(int id);

    @Query("SELECT positive_message FROM wellbeing_questions WHERE wellbeing_questions.wellbeing_question_id == :id")
    String getPositiveResponseById(int id);
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

    @Delete
    void delete(WellbeingQuestion wellbeingQuestion);
}
