package com.joshuarichardson.fivewaystowellbeing.storage;

public class WaysToWellbeingContract {
    // Activity record
    public static final String ACTIVITY_RECORD_TABLE_NAME = "activity_records";

    public static final String ACTIVITY_RECORD_ID = "id";
    public static final String ACTIVITY_RECORD_NAME = "name";
    public static final String ACTIVITY_RECORD_TYPE = "type";
    public static final String ACTIVITY_RECORD_TIMESTAMP = "timestamp";
    public static final String ACTIVITY_RECORD_DURATION = "duration";

    // Survey response
    public static final String SURVEY_RESPONSE_TABLE_NAME = "survey_response";

    public static final String SURVEY_RESPONSE_ID = "id";
    public static final String SURVEY_RESPONSE_TIMESTAMP = "timestamp";
    public static final String SURVEY_RESPONSE_WAY_TO_WELLBEING = "way_to_wellbeing";

    // Survey response element
    public static final String SURVEY_RESPONSE_ELEMENT_TABLE_NAME = "survey_response_element";

    public static final String SURVEY_RESPONSE_ELEMENT_ID = "id";
    public static final String SURVEY_RESPONSE_ELEMENT_QUESTION = "question";
    public static final String SURVEY_RESPONSE_ELEMENT_ANSWER = "answer";
    public static final String SURVEY_RESPONSE_ELEMENT_SURVEY_ID = "survey_id";

    // Survey response activity record
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_TABLE_NAME = "survey_activity";

    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_SURVEY_RESPONSE_ID = "survey_response_id";
    public static final String SURVEY_RESPONSE_ACTIVITY_RECORD_ACTIVITY_RECORD_ID = "activity_record_id";
}
