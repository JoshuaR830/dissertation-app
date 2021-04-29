package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.AppActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.AutomaticActivityDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.QuestionsToAskDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyQuestionSetDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingQuestionDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.WellbeingResultsDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AppActivity;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.AutomaticActivity;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import static com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase.DATABASE_VERSION_CODE;

/**
 * The room database that is injected whenever it is needed
 */
@Database(entities = {ActivityRecord.class, SurveyResponse.class, SurveyResponseActivityRecord.class, SurveyResponseElement.class, QuestionsToAsk.class, SurveyQuestionSet.class, WellbeingQuestion.class, WellbeingRecord.class, WellbeingResult.class, AutomaticActivity.class, AppActivity.class}, exportSchema = false, version = DATABASE_VERSION_CODE)
public abstract class WellbeingDatabase extends RoomDatabase {

    public static final int DATABASE_VERSION_CODE = 11;

    public abstract ActivityRecordDao activityRecordDao();
    public abstract SurveyResponseActivityRecordDao surveyResponseActivityRecordDao();
    public abstract SurveyResponseDao surveyResponseDao();
    public abstract SurveyResponseElementDao surveyResponseElementDao();
    public abstract QuestionsToAskDao questionsToAskDao();
    public abstract SurveyQuestionSetDao surveyQuestionSetDao();
    public abstract WellbeingQuestionDao wellbeingQuestionDao();
    public abstract WellbeingRecordDao wellbeingRecordDao();
    public abstract WellbeingResultsDao wellbeingResultsDao();
    public abstract AutomaticActivityDao physicalActivityDao();
    public abstract AppActivityDao appActivityDao();
}
