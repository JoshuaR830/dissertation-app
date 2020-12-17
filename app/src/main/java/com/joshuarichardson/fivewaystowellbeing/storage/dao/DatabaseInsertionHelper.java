package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import java.util.ArrayList;

public class DatabaseInsertionHelper {
    public static int[] insert(SurveyResponseElement[] surveyResponseElements, SurveyResponseElementDao surveyElementDao) {
        int[] responseElementIdList = new int[]{};

        for (SurveyResponseElement responseElement : surveyResponseElements) {
            surveyElementDao.insert(responseElement);
        }

        return responseElementIdList;
    }

    public static ArrayList<Integer> insert(ActivityRecord[] activityRecords, ActivityRecordDao activityRecordDao) {
        ArrayList<Integer> recordElementIdList = new ArrayList<Integer>();

        for (ActivityRecord activityRecord : activityRecords) {
            recordElementIdList.add((int) activityRecordDao.insert(activityRecord));
        }

        return recordElementIdList;
    }
}
