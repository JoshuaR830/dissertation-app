package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

// ToDo look into dependency injection
public class SurveyResponseElementHelper {
    public static int[] insert(SurveyResponseElement[] surveyResponseElements, SurveyResponseElementDao surveyElementDao) {
        int[] responseElementIdList = new int[]{};

        for (SurveyResponseElement responseElement : surveyResponseElements) {
            surveyElementDao.insert(responseElement);
        }

        return responseElementIdList;
    }
}
