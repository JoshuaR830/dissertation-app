package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import java.util.ArrayList;

public class DatabaseInsertionHelper {

    public static ArrayList<Long> insert(SurveyResponseElement[] surveyResponseElements, SurveyResponseElementDao surveyResponseElementDao) {
        ArrayList<Long> surveyResponseElementIdList = new ArrayList<>();

        for (SurveyResponseElement surveyResponseElement : surveyResponseElements) {
            surveyResponseElementIdList.add(surveyResponseElementDao.insert(surveyResponseElement));
        }

        return surveyResponseElementIdList;
    }

    public static ArrayList<Long> insert(ActivityRecord[] activityRecords, ActivityRecordDao activityRecordDao) {
        ArrayList<Long> activityRecordIdList = new ArrayList<>();

        for (ActivityRecord activityRecord : activityRecords) {
            activityRecordIdList.add(activityRecordDao.insert(activityRecord));
        }

        return activityRecordIdList;
    }

    public static ArrayList<Long> insert(SurveyResponse[] surveyResponses, SurveyResponseDao surveyResponseDao) {
        ArrayList<Long> surveyResponseIdList = new ArrayList<>();

        for (SurveyResponse surveyResponse : surveyResponses) {
            surveyResponseIdList.add(surveyResponseDao.insert(surveyResponse));
        }

        return surveyResponseIdList;
    }
}
