package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import org.junit.Before;
import org.junit.Test;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

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
    public void InsertingASurveyThenGettingTheSurveyById_ShouldReturnTheCorrectSurveyResponse() {

        // ToDo make SurveyResponse take timeStamp and wayToWellbeing
        SurveyResponse surveyResponse = new SurveyResponse();

        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);

        SurveyResponse actualSurveyResponse = this.surveyResponseDao.getSurveyResponseById(surveyId);

        // Need to confirm that the properties are correct
        assertThat(actualSurveyResponse.getSurveyResponseId())
                .isEqualTo(surveyId);

        assertThat(actualSurveyResponse.getTimestamp())
                .isEqualTo(surveyId);

        assertThat(actualSurveyResponse.getWayToWellbeing())
                .isEqualTo();
    }
}
