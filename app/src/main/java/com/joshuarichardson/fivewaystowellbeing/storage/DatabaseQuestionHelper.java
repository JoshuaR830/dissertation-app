package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.WellbeingQuestion;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;

public class DatabaseQuestionHelper {

    public static final int VERSION_NUMBER = 2;

    public static final int ACTIVITY_OF_TYPE_VALUE = 50;

    private static final int HIGH_VALUE = 20;
    private static final int MEDIUM_VALUE = 10;
    private static final int LOW_VALUE = 5;

    public static WellbeingQuestion[] getQuestions() {
        return new WellbeingQuestion[] {

            // Add questions for app
            new WellbeingQuestion(1, "Did you interact with anyone through the app?", "You can connect by using apps in common", "You could try to use the app together to form a connection", WaysToWellbeing.CONNECT.toString(), LOW_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(2, "Did you take regular breaks from your phone?", "Well done, it's not good to use apps for extended periods of time", "If possible, taking regular screen breaks is good", WaysToWellbeing.BE_ACTIVE.toString(), LOW_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(3, "Did you learn anything new?", "You learnt something new while using the app", "Try to learn something new when using apps", WaysToWellbeing.KEEP_LEARNING.toString(), HIGH_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(4, "Did the app give you a new perspective?", "Taking notice through!", "How could you use apps that help you to reflect on yourself and your surroundings", WaysToWellbeing.TAKE_NOTICE.toString(), MEDIUM_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(5, "Did you use this app to spread positivity?", "You gave by spreading positivity, nice one!", "Taking time to share positivity is a great way to give a boost to others and yourself", WaysToWellbeing.GIVE.toString(), MEDIUM_VALUE, ActivityType.APP.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for sport
            new WellbeingQuestion(6, "Did you exercise with another person?", "", "", WaysToWellbeing.CONNECT.toString(), HIGH_VALUE, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(7, "Were you active for 10+ minutes?", "", "", WaysToWellbeing.BE_ACTIVE.toString(), MEDIUM_VALUE, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(8, "Did you develop your skills?", "", "", WaysToWellbeing.KEEP_LEARNING.toString(), MEDIUM_VALUE, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(9, "Did you notice anything new in your surroundings?", "", "", WaysToWellbeing.TAKE_NOTICE.toString(), LOW_VALUE, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(10, "Did you greet or encourage someone else?", "", "", WaysToWellbeing.GIVE.toString(), LOW_VALUE, ActivityType.SPORT.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for hobby
            new WellbeingQuestion(11, "Did anyone else join in?", "", "", WaysToWellbeing.CONNECT.toString(), HIGH_VALUE, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(12, "Were you physically active?", "", "", WaysToWellbeing.BE_ACTIVE.toString(), MEDIUM_VALUE, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(13, "Did you advance your skills?", "", "", WaysToWellbeing.KEEP_LEARNING.toString(), HIGH_VALUE, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(14, "Did it reveal anything new about yourself?", "", "", WaysToWellbeing.TAKE_NOTICE.toString(), MEDIUM_VALUE, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(15, "Did you lend a helping hand?", "", "", WaysToWellbeing.GIVE.toString(), HIGH_VALUE, ActivityType.HOBBY.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for pet
            new WellbeingQuestion(16, "Did you talk or interact with your pet?", "positive message", "negative message", WaysToWellbeing.CONNECT.toString(), HIGH_VALUE, ActivityType.PET.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(17, "Did you play with your pet?", "positive message", "negative message", WaysToWellbeing.BE_ACTIVE.toString(), MEDIUM_VALUE, ActivityType.PET.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(18, "Did you learn a new fact about your pet?", "positive message", "negative message", WaysToWellbeing.KEEP_LEARNING.toString(), MEDIUM_VALUE, ActivityType.PET.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(19, "Did you notice a new behaviour?", "positive message", "negative message", WaysToWellbeing.TAKE_NOTICE.toString(), MEDIUM_VALUE, ActivityType.PET.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(20, "Did you spend quality time with your pet?", "positive message", "negative message", WaysToWellbeing.GIVE.toString(), HIGH_VALUE, ActivityType.PET.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for work
            new WellbeingQuestion(21, "Were you working with anyone else?", "positive message", "negative message", WaysToWellbeing.CONNECT.toString(), MEDIUM_VALUE, ActivityType.WORK.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(22, "Did you walk over to a colleague instead of communicating digitally?", "positive message", "negative message", WaysToWellbeing.BE_ACTIVE.toString(), LOW_VALUE, ActivityType.WORK.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(23, "Did you learn something new at work?", "positive message", "negative message", WaysToWellbeing.KEEP_LEARNING.toString(), HIGH_VALUE, ActivityType.WORK.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(24, "Has anything changed in your work environment?", "positive message", "negative message", WaysToWellbeing.TAKE_NOTICE.toString(), MEDIUM_VALUE, ActivityType.WORK.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(25, "Did you offer anyone a drink?", "positive message", "negative message", WaysToWellbeing.GIVE.toString(), HIGH_VALUE, ActivityType.WORK.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Add questions for learning
            new WellbeingQuestion(26, "Was it a group activity?", "positive message", "negative message", WaysToWellbeing.CONNECT.toString(), HIGH_VALUE, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(27, "Did you get up and move around?", "positive message", "negative message", WaysToWellbeing.BE_ACTIVE.toString(), MEDIUM_VALUE, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(28, "Did you gain new knowledge?", "positive message", "negative message", WaysToWellbeing.KEEP_LEARNING.toString(), HIGH_VALUE, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(29, "Did you have a new revelation?", "positive message", "negative message", WaysToWellbeing.TAKE_NOTICE.toString(), HIGH_VALUE, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(30, "Did you share knowledge with someone else?", "positive message", "negative message", WaysToWellbeing.GIVE.toString(), HIGH_VALUE, ActivityType.LEARNING.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Chores
            new WellbeingQuestion(31, "Was it a team effort?", "positive message", "negative message", WaysToWellbeing.CONNECT.toString(), HIGH_VALUE, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(32, "Was physical exertion required?", "positive message", "negative message", WaysToWellbeing.BE_ACTIVE.toString(), HIGH_VALUE, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(33, "Did you learn a new technique?", "positive message", "negative message", WaysToWellbeing.KEEP_LEARNING.toString(), MEDIUM_VALUE, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(34, "Did you do it in time to music?", "positive message", "negative message", WaysToWellbeing.TAKE_NOTICE.toString(), MEDIUM_VALUE, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(35, "Was it to help someone else?", "positive message", "negative message", WaysToWellbeing.GIVE.toString(), HIGH_VALUE, ActivityType.CHORES.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Cooking
            new WellbeingQuestion(36, "Did you cook with someone else?", "positive message", "negative message", WaysToWellbeing.CONNECT.toString(), HIGH_VALUE, ActivityType.COOKING.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(37, "Did you have to grab lots of ingredients?", "positive message", "negative message", WaysToWellbeing.BE_ACTIVE.toString(), LOW_VALUE, ActivityType.COOKING.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(38, "Did you try a new recipe or method?", "positive message", "negative message", WaysToWellbeing.KEEP_LEARNING.toString(), HIGH_VALUE, ActivityType.COOKING.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(39, "Did you use a utensil you'd forgotten you had?", "positive message", "negative message", WaysToWellbeing.TAKE_NOTICE.toString(), MEDIUM_VALUE, ActivityType.COOKING.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(40, "Was it a shared meal?", "positive message", "negative message", WaysToWellbeing.GIVE.toString(), HIGH_VALUE, ActivityType.COOKING.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Exercise
            new WellbeingQuestion(41, "Did you exercise with another person?", "", "", WaysToWellbeing.CONNECT.toString(), HIGH_VALUE, ActivityType.EXERCISE.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(42, "Were you active for 10+ minutes?", "", "", WaysToWellbeing.BE_ACTIVE.toString(), MEDIUM_VALUE, ActivityType.EXERCISE.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(43, "Did you develop your skills?", "", "", WaysToWellbeing.KEEP_LEARNING.toString(), MEDIUM_VALUE, ActivityType.EXERCISE.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(44, "Did you notice anything new in your surroundings?", "", "", WaysToWellbeing.TAKE_NOTICE.toString(), LOW_VALUE, ActivityType.EXERCISE.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(45, "Did you greet or encourage someone else?", "", "", WaysToWellbeing.GIVE.toString(), LOW_VALUE, ActivityType.EXERCISE.toString(), SurveyItemTypes.CHECKBOX.toString()),

            // Relaxation
            new WellbeingQuestion(46, "Did you relax with someone else?", "", "", WaysToWellbeing.CONNECT.toString(), HIGH_VALUE, ActivityType.RELAXATION.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(47, "Did you move in a relaxed manner?", "", "", WaysToWellbeing.BE_ACTIVE.toString(), MEDIUM_VALUE, ActivityType.RELAXATION.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(48, "Did it enhance your knowledge?", "", "", WaysToWellbeing.KEEP_LEARNING.toString(), MEDIUM_VALUE, ActivityType.RELAXATION.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(49, "Did you have a new experience?", "", "", WaysToWellbeing.TAKE_NOTICE.toString(), MEDIUM_VALUE, ActivityType.RELAXATION.toString(), SurveyItemTypes.CHECKBOX.toString()),
            new WellbeingQuestion(50, "Did you create a calming environment?", "", "", WaysToWellbeing.GIVE.toString(), LOW_VALUE, ActivityType.RELAXATION.toString(), SurveyItemTypes.CHECKBOX.toString()),
        };
    }
}
