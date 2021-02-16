package com.joshuarichardson.fivewaystowellbeing.storage.dao;

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

    @Query("UPDATE survey_activity SET note = :noteText WHERE survey_activity_id = :activitySurveyId")
    void updateNote(long activitySurveyId, String noteText);

    @Query("UPDATE survey_activity SET start_time = :startTime WHERE survey_activity_id = :activitySurveyId")
    void updateStartTime(long activitySurveyId, long startTime);

    @Query("UPDATE survey_activity SET end_time = :endTime WHERE survey_activity_id = :activitySurveyId")
    void updateEndTime(long activitySurveyId, long endTime);

    // ToDo Need a delete method
}
