package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

public class DatabaseInsertionHelper {
    public static int[] insert(SurveyResponseElement[] surveyResponseElements, SurveyResponseElementDao surveyElementDao) {
        int[] responseElementIdList = new int[]{};

        for (SurveyResponseElement responseElement : surveyResponseElements) {
            surveyElementDao.insert(responseElement);
        }

        return responseElementIdList;
    }

    public static int[] insert(ActivityRecord[] activityRecords, ActivityRecordDao activityRecordDao) {
        int[] responseElementIdList = new int[]{};

//        for (ActivityRecord responseElement : activityRecords) {
//            activityRecordDao.insert(responseElement);
//        }

        return responseElementIdList;
    }
}
