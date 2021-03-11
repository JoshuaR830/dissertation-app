package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.Passtime;
import com.joshuarichardson.fivewaystowellbeing.surveys.Question;

import java.util.Date;
import java.util.List;

public class WellbeingRecordInsertionHelper {
    public static void addPasstimeToSurvey(WellbeingDatabase db, long activitySurveyId, String activityType) {
        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<WellbeingQuestion> questions = db.wellbeingQuestionDao().getQuestionsByActivityType(activityType);
            int counter = 0;
            long timeNow = new Date().getTime();
            for(WellbeingQuestion question : questions) {
                db.wellbeingRecordDao().insert(new WellbeingRecord(false, timeNow, activitySurveyId, counter, question.getId()));
                counter ++;
            }
        });
    }

    // Must be called from within a write executor
    public static Passtime addPasstimeQuestions(WellbeingDatabase db, long activitySurveyId, String activityType, Passtime passtime, long time) {
        List<WellbeingQuestion> questions = db.wellbeingQuestionDao().getQuestionsByActivityType(activityType.toUpperCase());
        int counter = 0;
        for(WellbeingQuestion question : questions) {
            long wellbeingRecordId = db.wellbeingRecordDao().insert(new WellbeingRecord(false, time, activitySurveyId, counter, question.getId()));
            passtime.addQuestionToList(new Question(question.getQuestion(), wellbeingRecordId, false));
            counter ++;
        }

        return passtime;
    }
}
