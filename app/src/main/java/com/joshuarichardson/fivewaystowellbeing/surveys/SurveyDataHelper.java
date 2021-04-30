package com.joshuarichardson.fivewaystowellbeing.surveys;

import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A helper class to compress the raw data into data that can be displayed to the user
 */
public class SurveyDataHelper {

    /**
     * Take the raw data and convert it into data that is more useful for displaying a daily survey/wellbeing log
     * The survey is the same for each activity so the note and time can be retrieved from the first item
     * Raw data has activity instances with single questions - these must be collated to display a single activity with multiple questions
     *
     * @param surveyData All of the data required to display a survey
     * @return All the information combined in a way which facilitates displaying a survey
     */
    public static SurveyDay transform(List<RawSurveyData> surveyData) {

        // Don't allow for null pointer exceptions with data
        if(surveyData == null || surveyData.size() == 0) {
            return new SurveyDay(0, "", new ArrayList<>(), new HashMap<>());
        }

        ArrayList<Long> activityRecordIds = new ArrayList<>();
        HashMap<Long, ActivityInstance> list = new HashMap<>();

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
                list.put(activityId, new ActivityInstance(dataItem.getActivityName(), dataItem.getActivityNote(), dataItem.getActivityType(), dataItem.getWayToWellbeing(), dataItem.getSurveyActivityId(), dataItem.getStartTime(), dataItem.getEndTime(), dataItem.getEmotion(), dataItem.getIsDone()));
                activityRecordIds.add(activityId);
            }

            ActivityInstance activityInstance = list.get(activityId);

            if(activityInstance == null) {
                return null;
            }

            // Old activities don't have sub-activities (questions)
            if(dataItem.getWellbeingRecordId() < 0 || dataItem.getSurveyActivityId() < 0) {
                continue;
            }

            // Associate a question with the userActivity
            activityInstance.addQuestionToList(new Question(dataItem.getQuestion(), dataItem.getWellbeingRecordId(), dataItem.getUserInput()));
        }

        return new SurveyDay(surveyData.get(0).getDate(), surveyData.get(0).getSurveyNote(), activityRecordIds, list);
    }
}
