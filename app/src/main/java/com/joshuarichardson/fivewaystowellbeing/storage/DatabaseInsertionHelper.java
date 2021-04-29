package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.ArrayList;

/**
 * A helper to easily insert items into the database
 */
public class DatabaseInsertionHelper {

    /**
     * Insert a list of activity records
     *
     * @param activityRecords The list of records to insert
     * @param activityRecordDao the Dao to call insert on
     * @return A list of inserted ids
     */
    public static ArrayList<Long> insert(ActivityRecord[] activityRecords, ActivityRecordDao activityRecordDao) {
        ArrayList<Long> activityRecordIdList = new ArrayList<>();

        for (ActivityRecord activityRecord : activityRecords) {
            activityRecordIdList.add(activityRecordDao.insert(activityRecord));
        }

        return activityRecordIdList;
    }

    /**
     * Insert a list of survey responses
     *
     * @param surveyResponses The list of survey responses to insert
     * @param surveyResponseDao the Dao to call insert on
     * @return The list of survey response ids
     */
    public static ArrayList<Long> insert(SurveyResponse[] surveyResponses, SurveyResponseDao surveyResponseDao) {
        ArrayList<Long> surveyResponseIdList = new ArrayList<>();

        for (SurveyResponse surveyResponse : surveyResponses) {
            surveyResponseIdList.add(surveyResponseDao.insert(surveyResponse));
        }

        return surveyResponseIdList;
    }
}
