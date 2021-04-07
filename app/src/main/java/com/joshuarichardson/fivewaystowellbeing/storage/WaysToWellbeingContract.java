package com.joshuarichardson.fivewaystowellbeing.storage;

public class WaysToWellbeingContract {

    public static final String WELLBEING_DATABASE_NAME = "wellbeing_database";

    // Activity record
    public static final String ACTIVITY_RECORD_TABLE_NAME = "activity_records";

    public static final String ACTIVITY_RECORD_ID = "id";
    public static final String ACTIVITY_RECORD_NAME = "name";
    public static final String ACTIVITY_RECORD_TYPE = "type";
    public static final String ACTIVITY_RECORD_TIMESTAMP = "timestamp";
    public static final String ACTIVITY_RECORD_DURATION = "duration";
    public static final String ACTIVITY_RECORD_WAY_TO_WELLBEING = "way_to_wellbeing";
    public static final String ACTIVITY_RECORD_IS_HIDDEN = "is_hidden";

    // Survey response
    public static final String SURVEY_RESPONSE_TABLE_NAME = "survey_response";

    public static final String SURVEY_RESPONSE_ID = "id";
    public static final String SURVEY_RESPONSE_TIMESTAMP = "timestamp";
    public static final String SURVEY_RESPONSE_WAY_TO_WELLBEING = "way_to_wellbeing";
    public static final String SURVEY_RESPONSE_TITLE = "title";
    public static final String SURVEY_RESPONSE_DESCRIPTION = "description";


    //Wellbeing result
    public static final String WELLBEING_RESULT_TABLE_NAME = "wellbeing_result";

    public static final String WELLBEING_RESULT_ID = "id";
    public static final String WELLBEING_RESULT_CONNECT = "connect";
    public static final String WELLBEING_RESULT_BE_ACTIVE = "be_active";
    public static final String WELLBEING_RESULT_KEEP_LEARNING = "keep_learning";
    public static final String WELLBEING_RESULT_TAKE_NOTICE = "take_notice";
    public static final String WELLBEING_RESULT_GIVE = "give";
    public static final String WELLBEING_RESULT_TIMESTAMP = "timestamp";

    // Survey response element
    public static final String SURVEY_RESPONSE_ELEMENT_TABLE_NAME = "survey_response_element";

    public static final String SURVEY_RESPONSE_ELEMENT_ID = "id";
    public static final String SURVEY_RESPONSE_ELEMENT_QUESTION = "question";
    public static final String SURVEY_RESPONSE_ELEMENT_ANSWER = "answer";
    public static final String SURVEY_RESPONSE_ELEMENT_SURVEY_ID = "survey_id";

    // Survey response activity record
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_TABLE_NAME = "survey_activity";

    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_ACTIVITY_ID = "survey_activity_id";
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_RESPONSE_ID = "survey_response_id";
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_RECORD_ID = "activity_record_id";
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_SEQUENCE_NUMBER = "sequence_number";
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_NOTE = "note";
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_START_TIME = "start_time";
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_END_TIME = "end_time";
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_EMOTION = "emotion";
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_IS_DONE = "is_done";

    // Questions to ask
    public static final String QUESTIONS_TO_ASK_TABLE_NAME = "questions_to_ask";

    public static final String QUESTIONS_TO_ASK_ID = "id";
    public static final String QUESTIONS_TO_ASK_QUESTION = "question";
    public static final String QUESTIONS_TO_ASK_TYPE = "type";
    public static final String QUESTIONS_TO_ASK_REASON = "reason";
    public static final String QUESTIONS_TO_ASK_SEQUENCE_NUMBER = "sequence_number";
    public static final String QUESTIONS_TO_ASK_SET_ID = "set_id";
    public static final String QUESTIONS_TO_ASK_EXTRA_DATA = "extra_data";


    // Survey question set
    public static final String SURVEY_QUESTION_SET_TABLE_NAME = "survey_question_set";

    public static final String SURVEY_QUESTION_SET_ID = "set_id";
    public static final String SURVEY_QUESTION_SET_TIMESTAMP = "timestamp";
    public static final String SURVEY_QUESTION_SET_SURVEY_ID = "survey_id";

    // Wellbeing record
    public static final String WELLBEING_RECORDS_TABLE_NAME = "wellbeing_records";

    public static final String WELLBEING_RECORDS_ID = "wellbeing_record_id";
    public static final String WELLBEING_RECORDS_USER_INPUT = "user_input";
    public static final String WELLBEING_RECORDS_TIME = "time";
    public static final String WELLBEING_RECORDS_SURVEY_ACTIVITY_ID = "survey_response_activity_record_id";
    public static final String WELLBEING_RECORDS_SEQUENCE_NUMBER = "sequence_number";
    public static final String WELLBEING_RECORDS_QUESTION_ID = "question_id";

    // Wellbeing questions
    public static final String WELLBEING_QUESTIONS_TABLE_NAME = "wellbeing_questions";

    public static final String WELLBEING_QUESTIONS_ID = "wellbeing_question_id";
    public static final String WELLBEING_QUESTIONS_QUESTION = "question";
    public static final String WELLBEING_QUESTIONS_POSITIVE_MESSAGE = "positive_message";
    public static final String WELLBEING_QUESTIONS_NEGATIVE_MESSAGE = "negative_message";
    public static final String WELLBEING_QUESTIONS_WAY_TO_WELLBEING = "way_to_wellbeing";
    public static final String WELLBEING_QUESTIONS_WEIGHTING = "weighting";
    public static final String WELLBEING_QUESTIONS_ACTIVITY_TYPE = "activity_type";
    public static final String WELLBEING_QUESTIONS_INPUT_TYPE = "input_type";

    // Physical activities
    public static final String PHYSICAL_ACTIVITY_TABLE = "physical_activity";

    public static final String PHYSICAL_ACTIVITY_TYPE = "activity_type";
    public static final String PHYSICAL_ACTIVITY_START_TIME = "start_time";
    public static final String PHYSICAL_ACTIVITY_END_TIME = "end_time";
    public static final String PHYSICAL_ACTIVITY_ACTIVITY_ID = "activity_id";
    public static final String PHYSICAL_ACTIVITY_IS_PENDING = "is_pending";
    public static final String PHYSICAL_ACTIVITY_IS_CONFIRMED = "is_notification_confirmed";
}
