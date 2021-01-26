package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.QuestionsToAskDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyQuestionSetDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ActivityRecord.class, SurveyResponse.class, SurveyResponseActivityRecord.class, SurveyResponseElement.class, QuestionsToAsk.class, SurveyQuestionSet.class}, exportSchema = false, version = 3)
public abstract class WellbeingDatabase extends RoomDatabase {

    public abstract ActivityRecordDao activityRecordDao();
    public abstract SurveyResponseActivityRecordDao surveyResponseActivityRecordDao();
    public abstract SurveyResponseDao surveyResponseDao();
    public abstract SurveyResponseElementDao surveyResponseElementDao();
    public abstract QuestionsToAskDao questionsToAskDao();
    public abstract SurveyQuestionSetDao surveyQuestionSetDao();
}
