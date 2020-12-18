package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import java.util.ArrayList;

public class DatabaseInsertionHelper {

    public static ArrayList<Integer> insert(SurveyResponseElement[] surveyResponseElements, SurveyResponseElementDao surveyResponseElementDao) {
        ArrayList<Integer> responseElementIdList = new ArrayList<>();

        for (SurveyResponseElement surveyResponseElement : surveyResponseElements) {
            responseElementIdList.add((int) surveyResponseElementDao.insert(surveyResponseElement));
        }

        return responseElementIdList;
    }

    public static ArrayList<Integer> insert(ActivityRecord[] activityRecords, ActivityRecordDao activityRecordDao) {
        ArrayList<Integer> recordElementIdList = new ArrayList<>();

        for (ActivityRecord activityRecord : activityRecords) {
            recordElementIdList.add((int) activityRecordDao.insert(activityRecord));
        }

        return recordElementIdList;
    }

    public static ArrayList<Integer> insert(SurveyResponse[] surveyResponses, SurveyResponseDao surveyResponseDao) {
        ArrayList<Integer> recordElementIdList = new ArrayList<>();

        for (SurveyResponse surveyResponse : surveyResponses) {
            recordElementIdList.add((int) surveyResponseDao.insert(surveyResponse));
        }

        return recordElementIdList;
    }
}
