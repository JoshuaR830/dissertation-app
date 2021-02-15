package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;

public class DatabaseQuestionHelper {

    private static final int HIGH_VALUE = 20;
    private static final int MEDIUM_VALUE = 10;
    private static final int LOW_VALUE = 5;

    public static WellbeingQuestion[] getQuestions() {
        return new WellbeingQuestion[] {

            // Add questions for app
            new WellbeingQuestion(1, "Did you learn anything new?", "You learnt something new while using the app", "Try to learn something new when using apps", WaysToWellbeing.KEEP_LEARNING.toString(), HIGH_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(2, "Did someone else use it at the same time?", "You can connect by doing things in common", "You could try to use the app together to form a connection", WaysToWellbeing.CONNECT.toString(), LOW_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(3, "Did you take regular breaks from your phone?", "Well done, it's not good to use apps for extended periods of time", "If possible, taking regular screen breaks is good", WaysToWellbeing.BE_ACTIVE.toString(), LOW_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(4, "Did you use this app to spread positivity?", "You gave by spreading positivity, nice one!", "Taking time to share positivity is a great way to give a boost to others and yourself", WaysToWellbeing.GIVE.toString(), MEDIUM_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(5, "Did using this app help you to appreciate something about yourself or the world differently?", "Taking notice through!", "How could you use apps that help you to reflect on yourself and your surroundings", WaysToWellbeing.TAKE_NOTICE.toString(), MEDIUM_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for sport
            new WellbeingQuestion(6, "question", "positive message", "negative message", WaysToWellbeing.BE_ACTIVE.toString(), LOW_VALUE, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for hobby
            new WellbeingQuestion(11, "question", "positive message", "negative message", WaysToWellbeing.KEEP_LEARNING.toString(), MEDIUM_VALUE, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for pet
            new WellbeingQuestion(16, "question", "positive message", "negative message", WaysToWellbeing.CONNECT.toString(), HIGH_VALUE, ActivityType.PET.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for work
            new WellbeingQuestion(21, "question", "positive message", "negative message", WaysToWellbeing.KEEP_LEARNING.toString(), MEDIUM_VALUE, ActivityType.WORK.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for learning
            new WellbeingQuestion(26, "question", "positive message", "negative message", WaysToWellbeing.KEEP_LEARNING.toString(), LOW_VALUE, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()),
        };
    }
}
