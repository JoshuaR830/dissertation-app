package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface WellbeingQuestionDao {
    @Insert
    long insert(WellbeingQuestion wellbeingQuestion);

    @Query("SELECT * FROM wellbeing_questions WHERE id == :id")
    WellbeingQuestion getQuestionById(int id);

    @Query("SELECT * FROM wellbeing_questions WHERE activity_type == :activityType")
    List<WellbeingQuestion> getQuestionsByActivityType(String activityType);

    @Query("SELECT negative_message FROM wellbeing_questions WHERE wellbeing_questions.id == :id")
    String getNegativeResponseById(int id);

    @Query("SELECT positive_message FROM wellbeing_questions WHERE wellbeing_questions.id == :id")
    String getPositiveResponseById(int id);
}
