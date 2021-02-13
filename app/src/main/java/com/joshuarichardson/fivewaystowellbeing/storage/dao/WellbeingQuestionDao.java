package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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

    @Query("SELECT wellbeing_questions.way_to_wellbeing AS wayToWellbeing, SUM(wellbeing_questions.weighting) AS value FROM wellbeing_questions " +
        "INNER JOIN wellbeing_records ON wellbeing_records.question_id == wellbeing_questions.wellbeing_question_id " +
        "WHERE  user_input == 1 AND wellbeing_records.time BETWEEN :startTime AND :endTime " +
        "GROUP BY wellbeing_questions.way_to_wellbeing")
    LiveData<List<WellbeingGraphItem>> getWaysToWellbeingBetweenTimes(long startTime, long endTime);

    @Query("SELECT wellbeing_questions.way_to_wellbeing AS wayToWellbeing, SUM(wellbeing_questions.weighting) AS value FROM wellbeing_questions " +
        "INNER JOIN wellbeing_records ON wellbeing_records.question_id == wellbeing_questions.wellbeing_question_id " +
        "WHERE  user_input == 1 AND wellbeing_records.time BETWEEN :startTime AND :endTime " +
        "GROUP BY wellbeing_questions.way_to_wellbeing")
    List<WellbeingGraphItem> getWaysToWellbeingBetweenTimesNotLive(long startTime, long endTime);

    @Delete
    void delete(WellbeingQuestion wellbeingQuestion);
}
