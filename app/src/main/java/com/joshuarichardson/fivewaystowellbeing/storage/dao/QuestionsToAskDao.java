package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface QuestionsToAskDao {
    @Insert
    long insert(QuestionsToAsk questionsToAsk);

    @Query("SELECT * FROM questions_to_ask WHERE id = :questionRecordId")
    LiveData<List<QuestionsToAsk>> getQuestionsById(long questionRecordId);

    @Query("SELECT * FROM questions_to_ask WHERE set_id = :setId")
    LiveData<List<QuestionsToAsk>> getQuestionsBySetId(int setId);
}
