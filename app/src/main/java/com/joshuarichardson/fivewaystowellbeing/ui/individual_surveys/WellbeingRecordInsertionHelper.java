package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.UserActivity;
import com.joshuarichardson.fivewaystowellbeing.surveys.Question;

import java.util.Calendar;
import java.util.List;

public class WellbeingRecordInsertionHelper {
    public static void addActivityToSurvey(WellbeingDatabase db, long activitySurveyId, String activityType) {
        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
            List<WellbeingQuestion> questions = db.wellbeingQuestionDao().getQuestionsByActivityType(activityType);
            int counter = 0;
            long timeNow = Calendar.getInstance().getTimeInMillis();
            for(WellbeingQuestion question : questions) {
                db.wellbeingRecordDao().insert(new WellbeingRecord(false, timeNow, activitySurveyId, counter, question.getId()));
                counter ++;
            }
        });
    }

    // Must be called from within a write executor
    public static UserActivity addActivityQuestions(WellbeingDatabase db, long activitySurveyId, String activityType, UserActivity userActivity, long time) {
        List<WellbeingQuestion> questions = db.wellbeingQuestionDao().getQuestionsByActivityType(activityType.toUpperCase());
        int counter = 0;
        for(WellbeingQuestion question : questions) {
            long wellbeingRecordId = db.wellbeingRecordDao().insert(new WellbeingRecord(false, time, activitySurveyId, counter, question.getId()));
            userActivity.addQuestionToList(new Question(question.getQuestion(), wellbeingRecordId, false));
            counter ++;
        }

        return userActivity;
    }
}
