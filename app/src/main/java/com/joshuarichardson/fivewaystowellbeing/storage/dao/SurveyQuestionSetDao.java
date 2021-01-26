package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface SurveyQuestionSetDao {
    @Insert
    long insert(SurveyQuestionSet surveyQuestionSet);

    @Query("SELECT * FROM survey_question_set WHERE survey_id = 0")
    LiveData<List<SurveyQuestionSet>> getUnansweredSurveyQuestionSets();

    @Query("SELECT * FROM survey_question_set WHERE set_id = :setId")
    LiveData<List<SurveyQuestionSet>> getSurveyQuestionSetById(long setId);

    @Query("UPDATE survey_question_set SET survey_id = :surveyId WHERE set_id = :setId")
    void updateSetWithCompletedSurveyId(long setId, int surveyId);

    @Query("SELECT * FROM questions_to_ask as qta INNER JOIN survey_question_set as sqs ON sqs.set_id == qta.set_id WHERE qta.set_id = :setId ORDER BY qta.sequence_number ASC")
    LiveData<List<QuestionsToAsk>> getQuestionsForSurveyQuestionSetWithId(long setId);
}
