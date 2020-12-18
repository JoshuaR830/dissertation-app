package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import java.util.ArrayList;

public class DatabaseInsertionHelper {

    public static ArrayList<Integer> insert(SurveyResponseElement[] surveyResponseElements, SurveyResponseElementDao surveyResponseElementDao) {
        ArrayList<Integer> surveyResponseElementIdList = new ArrayList<>();

        for (SurveyResponseElement surveyResponseElement : surveyResponseElements) {
            surveyResponseElementIdList.add((int) surveyResponseElementDao.insert(surveyResponseElement));
        }

        return surveyResponseElementIdList;
    }

    public static ArrayList<Integer> insert(ActivityRecord[] activityRecords, ActivityRecordDao activityRecordDao) {
        ArrayList<Integer> activityRecordIdList = new ArrayList<>();

        for (ActivityRecord activityRecord : activityRecords) {
            activityRecordIdList.add((int) activityRecordDao.insert(activityRecord));
        }

        return activityRecordIdList;
    }

    public static ArrayList<Integer> insert(SurveyResponse[] surveyResponses, SurveyResponseDao surveyResponseDao) {
        ArrayList<Integer> surveyResponseIdList = new ArrayList<>();

        for (SurveyResponse surveyResponse : surveyResponses) {
            surveyResponseIdList.add((int) surveyResponseDao.insert(surveyResponse));
        }

        return surveyResponseIdList;
    }
}
