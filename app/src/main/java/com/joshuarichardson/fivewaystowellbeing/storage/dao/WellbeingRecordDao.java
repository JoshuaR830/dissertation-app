package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.LimitedRawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface WellbeingRecordDao {
    @Insert
    long insert(WellbeingRecord wellbeingRecord);

    @Query("SELECT * FROM wellbeing_records WHERE survey_response_activity_record_id = :surveyActivityId ORDER BY sequence_number ASC")
    List<WellbeingRecord> getWellbeingRecordsBySurveyActivityId(long surveyActivityId);

    @Query("SELECT * FROM wellbeing_records WHERE wellbeing_record_id = :id")
    WellbeingRecord getWellbeingRecordById(long id);

    @Query("SELECT survey_response.timestamp AS date, survey_response.description AS surveyNote, survey_activity.note AS activityNote, activity_records.name AS activityName, survey_activity.survey_activity_id AS surveyActivityId, wellbeing_questions.question AS question, wellbeing_records.wellbeing_record_id AS wellbeingRecordId, wellbeing_records.user_input AS userInput, activity_records.type AS activityType " +
        "FROM wellbeing_records " +
        "INNER JOIN wellbeing_questions ON wellbeing_records.question_id == wellbeing_questions.wellbeing_question_id " + // This is for getting the questions
        "INNER JOIN survey_activity ON survey_activity.survey_activity_id == wellbeing_records.survey_response_activity_record_id " + // This is for getting the wellbeing record associated with a survey activity pair
        "INNER JOIN activity_records ON activity_records.id == survey_activity.activity_record_id " + // This is for getting the activity
        "INNER JOIN survey_response ON survey_activity.survey_response_id == survey_response.id " + // This is for getting the survey
        "WHERE survey_response.id == :surveyId ORDER BY survey_activity.sequence_number ASC, wellbeing_records.sequence_number ASC")
    List<RawSurveyData> getDataBySurvey(long surveyId);

    @Query("SELECT survey_response.timestamp AS date, survey_response.description AS surveyNote, activity_records.name AS activityName, activity_records.type AS activityType " +
        "FROM survey_activity " +
        "INNER JOIN activity_records ON activity_records.id == survey_activity.activity_record_id " + // This is for getting the activity
        "INNER JOIN survey_response ON survey_activity.survey_response_id == survey_response.id " + // This is for getting the survey
        "WHERE survey_response.id == :surveyId ORDER BY activity_records.timestamp")
    List<LimitedRawSurveyData> getLimitedDataBySurvey(long surveyId);

    @Query("UPDATE wellbeing_records SET user_input = :isChecked WHERE wellbeing_record_id = :wellbeingRecordId")
    void checkItem(long wellbeingRecordId, boolean isChecked);

    @Delete
    void delete(WellbeingRecord wellbeingRecord);
}
