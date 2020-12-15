package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class SurveyResponseElementTests {

    WellbeingDatabase wellbeingDb;
    private SurveyResponseDao surveyResponseDao;
    private SurveyResponseElementDao surveyResponseElementDao;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.surveyResponseElementDao = this.wellbeingDb.surveyResponseElementDao();
        this.surveyResponseDao = this.wellbeingDb.surveyResponseDao();
    }

    @Test
    public void insertingSurveyResponseElement_ThenGettingResponseElementById_ShouldReturnTheCorrectSurveyResponse() {

    }

    @Test
    public void insertingMultipleSurveyResponseElementsIndividually_ThenRetrievingBySurveyId_ShouldReturnTheCorrectNumberOfSurveys() {
        // ToDo make ways to wellbeing an enum
        SurveyResponse surveyResponse = new SurveyResponse(6378568, "Be active");
        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);

        SurveyResponseElement surveyResponseElement1 = new SurveyResponseElement();
        SurveyResponseElement surveyResponseElement2 = new SurveyResponseElement();
        SurveyResponseElement surveyResponseElement3 = new SurveyResponseElement();

        this.surveyResponseElementDao.insert(surveyResponseElement1);
        this.surveyResponseElementDao.insert(surveyResponseElement2);
        this.surveyResponseElementDao.insert(surveyResponseElement3);
    }

    @Test
    public void insertingMultipleSurveyResponseElementsFromAList_ThenRetrievingBySurveyId_ShouldReturnTheCorrectNumberOfSurveys() {
        // ToDo make ways to wellbeing an enum
        SurveyResponse surveyResponse = new SurveyResponse(6378568, "Be active");
        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);

        SurveyResponseElement[] surveyResponseElements = new SurveyResponseElement[] {
                new SurveyResponseElement(),
                new SurveyResponseElement(),
                new SurveyResponseElement()
        };

        this.surveyResponseElementDao.insert(surveyResponseElements);
    }

    @Test
    public void whenRetrievingBySurveyIdThatDoesNotExist_ShouldReturnEmptyList() {
        List<SurveyResponseElement> surveyResponseElements = this.surveyResponseElementDao.getBySurveyId();

        assertThat(surveyResponseElements).isNotNull();
        assertThat(surveyResponseElements).isEmpty();
    }
}
