package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.ActivityInstance;
import com.joshuarichardson.fivewaystowellbeing.surveys.Question;

import java.util.Calendar;
import java.util.List;

/**
 * Helper to insert wellbeing records intot the table and make the data available for each activity
 */
public class WellbeingRecordInsertionHelper {
    /**
     * Insert a wellbeing record into the table
     * The wellbeing record should be related to the activity type
     *
     * @param db An instance of the database
     * @param activitySurveyId An existing activity survey
     * @param activityType The type of activity
     */
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

    /**
     * Add questions to the activity instance and add wellbeing records
     * Must be called from within a write executor
     *
     * @param db An instance of the database
     * @param activitySurveyId An existing activity survey
     * @param activityType The type of activity
     * @param activityInstance The instance of the activity that will have a question
     * @param time The time in milliseconds when it was added
     * @return The activity instance with all of the questions
     */
    public static ActivityInstance addActivityQuestions(WellbeingDatabase db, long activitySurveyId, String activityType, ActivityInstance activityInstance, long time) {
        List<WellbeingQuestion> questions = db.wellbeingQuestionDao().getQuestionsByActivityType(activityType.toUpperCase());
        int counter = 0;
        for(WellbeingQuestion question : questions) {
            long wellbeingRecordId = db.wellbeingRecordDao().insert(new WellbeingRecord(false, time, activitySurveyId, counter, question.getId()));
            activityInstance.addQuestionToList(new Question(question.getQuestion(), wellbeingRecordId, false));
            counter ++;
        }

        return activityInstance;
    }
}
