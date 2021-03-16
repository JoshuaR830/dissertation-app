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

@Dao
public interface SurveyResponseActivityRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SurveyResponseActivityRecord surveyResponseActivityRecord);

    @Query("SELECT * FROM survey_activity WHERE survey_response_id = :surveyResponseId ORDER BY sequence_number")
    List<SurveyResponseActivityRecord> getActivityRecordDetailsForSurvey(long surveyResponseId);

    @Query("SELECT * FROM activity_records " +
            "INNER JOIN survey_activity ON activity_records.id=survey_activity.activity_record_id " +
            "WHERE survey_activity.survey_response_id = :surveyId ORDER BY sequence_number")
    List<ActivityRecord> getActivitiesBySurveyId(long surveyId);

    @Query("SELECT * FROM survey_response " +
            "INNER JOIN survey_activity ON survey_response.id = survey_activity.survey_response_id " +
            "WHERE survey_activity.activity_record_id = :activityId")
    LiveData<List<SurveyResponse>> getSurveyByActivityId(long activityId);

    @Query("SELECT * FROM survey_activity WHERE survey_activity_id = :surveyActivityId")
    SurveyResponseActivityRecord getSurveyActivityById(long surveyActivityId);

    @Query("UPDATE survey_activity SET note = :noteText WHERE survey_activity_id = :surveyActivityId")
    void updateNote(long surveyActivityId, String noteText);

    @Query("UPDATE survey_activity SET start_time = :startTime WHERE survey_activity_id = :surveyActivityId")
    void updateStartTime(long surveyActivityId, long startTime);

    @Query("UPDATE survey_activity SET end_time = :endTime WHERE survey_activity_id = :surveyActivityId")
    void updateEndTime(long surveyActivityId, long endTime);

    @Query("UPDATE survey_activity SET emotion = :emotion WHERE survey_activity_id = :surveyActivityId")
    void updateEmotion(long surveyActivityId, int emotion);

    @Query("UPDATE survey_activity SET is_done = :isDone WHERE survey_activity_id = :surveyActivityId")
    void updateIsDone(long surveyActivityId, boolean isDone);

    // Count the number of emotion items and the cumulative emotion score
    @Query("SELECT COUNT(*) AS emotionCount, SUM(emotion) AS totalValue FROM survey_activity WHERE survey_response_id = :surveyId AND emotion != 0")
    LiveData<SurveyCountItem> getEmotions(long surveyId);

    @Query("SELECT COUNT(*) AS activityCount FROM survey_activity WHERE survey_response_id = :surveyId")
    int getItemCount(long surveyId);

    @Query("DELETE FROM survey_activity WHERE survey_activity_id = :activitySurveyId")
    void deleteById(long activitySurveyId);

    @Query("SELECT activity_records.id AS activityId, count(*) AS count FROM activity_records " +
        "JOIN survey_activity ON survey_activity.activity_record_id = activity_records.id " +
        "INNER JOIN survey_response ON survey_activity.survey_response_id = survey_response.id " +
        "WHERE activity_records.is_hidden = 0 AND survey_response.timestamp BETWEEN :startTime AND :endTime " +
        "GROUP BY activity_records.id " +
        "ORDER BY count(*) DESC")
    List<ActivityStats> getActivityFrequencyBetweenTimes(long startTime, long endTime);

    @Query("SELECT activity_records.id AS activityId, count(*) AS count FROM activity_records " +
        "JOIN survey_activity ON survey_activity.activity_record_id = activity_records.id " +
        "INNER JOIN survey_response ON survey_activity.survey_response_id = survey_response.id " +
        "WHERE activity_records.is_hidden = 0 AND survey_response.timestamp BETWEEN :startTime AND :endTime AND activity_records.way_to_wellbeing = :wayToWellbeing " +
        "GROUP BY activity_records.id " +
        "ORDER BY count(*) DESC")
    List<ActivityStats> getActivityFrequencyByWellbeingTypeBetweenTimes(long startTime, long endTime, String wayToWellbeing);
}
