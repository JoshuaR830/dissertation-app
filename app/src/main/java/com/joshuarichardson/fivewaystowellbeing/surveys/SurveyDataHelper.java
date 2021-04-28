package com.joshuarichardson.fivewaystowellbeing.surveys;

import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveyDataHelper {
    public static SurveyDay transform(List<RawSurveyData> data) {

        // Don't allow for null pointer exceptions with data
        if(data == null || data.size() == 0) {
            return new SurveyDay(0, "", new ArrayList<>(), new HashMap<>());
        }

        ArrayList<Long> activityRecordIds = new ArrayList<>();
        HashMap<Long, UserActivity> list = new HashMap<>();

        int counter = 0;
        for(RawSurveyData d : data) {
            counter --;
            // This only works when there is a survey activity Id - so won't work for old activities
            long activityId = d.getSurveyActivityId();

            // Gradually increase the negative so still invalid id, but also allows it to be a unique key
            if(d.getSurveyActivityId() < 0) {
                activityId = counter;
            }

            // If the activity doesn't exist in here already - add it
            if (!list.containsKey(activityId)) {
                list.put(activityId, new UserActivity(d.getActivityName(), d.getActivityNote(), d.getActivityType(), d.getWayToWellbeing(), d.getSurveyActivityId(), d.getStartTime(), d.getEndTime(), d.getEmotion(), d.getIsDone()));
                activityRecordIds.add(activityId);
            }

            UserActivity userActivity = list.get(activityId);

            if(userActivity == null) {
                return null;
            }

            // ToDo Should probably add a test case for this
            if(d.getWellbeingRecordId() < 0 || d.getSurveyActivityId() < 0) {
                continue;
            }
            userActivity.addQuestionToList(new Question(d.getQuestion(), d.getWellbeingRecordId(), d.getUserInput()));
        }

        return new SurveyDay(data.get(0).getDate(), data.get(0).getSurveyNote(), activityRecordIds, list);
    }
}
