package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface WellbeingResultsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(WellbeingResult result);

    @Query("UPDATE wellbeing_result SET connect = :connectValue, be_active = :beActiveValue, keep_learning = :keepLearningValue, take_notice = :takeNoticeValue, give = :giveValue WHERE id = :surveyId")
    void updateWaysToWellbeing(long surveyId, int connectValue, int beActiveValue, int keepLearningValue, int takeNoticeValue, int giveValue);

    @Query("SELECT * FROM wellbeing_result WHERE id = :surveyId")
    WellbeingResult getResultsBySurveyId(long surveyId);

    @Query("SELECT * FROM wellbeing_result WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    List<WellbeingResult> getResultsByTimestampRange(long startTime, long endTime);
}
