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

    @Query("SELECT * FROM activity_records " +
            "INNER JOIN survey_activity ON activity_records.id=survey_activity.activity_record_id " +
            "WHERE survey_activity.survey_response_id = :surveyId")
    List<ActivityRecord> getActivitiesBySurveyId(long surveyId);

    @Query("SELECT * FROM survey_response " +
            "INNER JOIN survey_activity ON survey_response.id = survey_activity.survey_response_id " +
            "WHERE survey_activity.activity_record_id = :activityId")
    LiveData<List<SurveyResponse>> getSurveyByActivityId(long activityId);

    // ToDo Need a delete method
}
