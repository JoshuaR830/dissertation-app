package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingResult;

import java.util.List;

public class WellbeingAverageValueHelper {
    public static WellbeingValues processResults(List<WellbeingResult> surveyResponses) {
        return new WellbeingValues(surveyResponses);
    }
}
