package com.joshuarichardson.fivewaystowellbeing.surveys;

import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveyDataHelper {
    public static SurveyDay transform(List<RawSurveyData> surveyData) {

        // Don't allow for null pointer exceptions with data
        if(surveyData == null || surveyData.size() == 0) {
            return new SurveyDay(0, "", new ArrayList<>(), new HashMap<>());
        }

        ArrayList<Long> activityRecordIds = new ArrayList<>();
        HashMap<Long, UserActivity> list = new HashMap<>();

        int counter = 0;
        for(RawSurveyData dataItem : surveyData) {
            counter --;
            // This only works when there is a survey activity Id - so won't work for old activities
            long activityId = dataItem.getSurveyActivityId();

            // Gradually increase the negative so still invalid id, but also allows it to be a unique key
            if(dataItem.getSurveyActivityId() < 0) {
                activityId = counter;
            }

            // If the activity doesn't exist in here already - add it
            if (!list.containsKey(activityId)) {
                list.put(activityId, new UserActivity(dataItem.getActivityName(), dataItem.getActivityNote(), dataItem.getActivityType(), dataItem.getWayToWellbeing(), dataItem.getSurveyActivityId(), dataItem.getStartTime(), dataItem.getEndTime(), dataItem.getEmotion(), dataItem.getIsDone()));
                activityRecordIds.add(activityId);
            }

            UserActivity userActivity = list.get(activityId);

            if(userActivity == null) {
                return null;
            }

            // ToDo Should probably add a test case for this
            if(dataItem.getWellbeingRecordId() < 0 || dataItem.getSurveyActivityId() < 0) {
                continue;
            }
            userActivity.addQuestionToList(new Question(dataItem.getQuestion(), dataItem.getWellbeingRecordId(), dataItem.getUserInput()));
        }

        return new SurveyDay(surveyData.get(0).getDate(), surveyData.get(0).getSurveyNote(), activityRecordIds, list);
    }
}
