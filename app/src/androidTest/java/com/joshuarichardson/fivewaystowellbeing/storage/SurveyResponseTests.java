package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class SurveyResponseTests {

    private WellbeingDatabase wellbeingDb;
    private SurveyResponseDao surveyResponseDao;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.surveyResponseDao = wellbeingDb.surveyResponseDao();
    }

    @Test
    public void InsertingASurvey_ThenGettingTheSurveyById_ShouldReturnTheCorrectSurveyResponse() {

        // ToDo make SurveyResponse take timeStamp and wayToWellbeing
        SurveyResponse surveyResponse = new SurveyResponse(1607960240, "Be active");

        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);

        List<SurveyResponse> surveyResponses = this.surveyResponseDao.getSurveyResponseById(surveyId);

        assertWithMessage("There should be at least 1 item in the list")
                .that(surveyResponses.size())
                .isGreaterThan(0);

        SurveyResponse actualSurveyResponse = surveyResponses.get(0);

        // Need to confirm that the properties are correct
        assertThat(actualSurveyResponse.getSurveyResponseId())
                .isEqualTo(surveyId);

        assertThat(actualSurveyResponse.getSurveyResponseTimestamp())
                .isEqualTo(1607960240);

        // ToDo ways to wellbeing should definitely be an enum
        assertThat(actualSurveyResponse.getSurveyResponseWayToWellbeing())
                .isEqualTo("Be active");
    }
}
