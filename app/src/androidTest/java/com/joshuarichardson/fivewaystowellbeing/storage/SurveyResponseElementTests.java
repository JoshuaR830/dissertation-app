package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.DatabaseInsertionHelper;
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

        int surveyId = 12345678;
        String question = "What is the question?";
        String answer = "I don't know";

        SurveyResponseElement surveyResponseElement1 = new SurveyResponseElement(surveyId, question, answer);
        SurveyResponseElement surveyResponseElement2 = new SurveyResponseElement(surveyId + 1, "what is happening?", "I don't know");

        // Inserting in this order means that the first element is not the one that the test is interested in
        surveyResponseElementDao.insert(surveyResponseElement2);
        int elementId1 = (int) surveyResponseElementDao.insert(surveyResponseElement1);

        List<SurveyResponseElement> surveyResponseElements = this.surveyResponseElementDao.getSurveyResponseElementBySurveyResponseElementId(surveyId);
        SurveyResponseElement actualSurveyElement = surveyResponseElements.get(0);

        assertThat(actualSurveyElement.getId()).isEqualTo(elementId1);
        assertThat(actualSurveyElement.getSurveyId()).isEqualTo(surveyId);
        assertThat(actualSurveyElement.getQuestion()).isEqualTo(question);
        assertThat(actualSurveyElement.getAnswer()).isEqualTo(answer);
    }

    @Test
    public void insertingMultipleSurveyResponseElementsIndividually_ThenRetrievingBySurveyId_ShouldReturnTheCorrectNumberOfSurveys() {
        SurveyResponse surveyResponse = new SurveyResponse(6378568, WaysToWellbeing.BE_ACTIVE);
        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);

        SurveyResponseElement surveyResponseElement1 = new SurveyResponseElement(surveyId, "", "");
        SurveyResponseElement surveyResponseElement2 = new SurveyResponseElement(surveyId, "", "");
        SurveyResponseElement surveyResponseElement3 = new SurveyResponseElement(surveyId, "", "");
        SurveyResponseElement surveyResponseElement4 = new SurveyResponseElement(surveyId + 1, "", "");

        this.surveyResponseElementDao.insert(surveyResponseElement1);
        this.surveyResponseElementDao.insert(surveyResponseElement2);
        this.surveyResponseElementDao.insert(surveyResponseElement3);
        this.surveyResponseElementDao.insert(surveyResponseElement4);

        List<SurveyResponseElement> surveyResponseElementsResponse = this.surveyResponseElementDao.getBySurveyResponseElementBySurveyResponseId(surveyId);
        assertThat(surveyResponseElementsResponse.size()).isEqualTo(3);
    }

    @Test
    public void insertingMultipleSurveyResponseElementsFromAList_ThenRetrievingBySurveyId_ShouldReturnTheCorrectNumberOfSurveys() {
        SurveyResponse surveyResponse = new SurveyResponse(6378568, WaysToWellbeing.BE_ACTIVE);
        int surveyId = (int) this.surveyResponseDao.insert(surveyResponse);

        SurveyResponseElement[] surveyResponseElements = new SurveyResponseElement[] {
                new SurveyResponseElement(surveyId, "", ""),
                new SurveyResponseElement(surveyId, "", ""),
                new SurveyResponseElement(surveyId, "", ""),
                new SurveyResponseElement(surveyId + 1, "", ""),
                new SurveyResponseElement(surveyId + 2, "", ""),
        };

        DatabaseInsertionHelper.insert(surveyResponseElements, this.surveyResponseElementDao);

        List<SurveyResponseElement> surveyResponseElementsResponse = this.surveyResponseElementDao.getBySurveyResponseElementBySurveyResponseId(surveyId);

        assertThat(surveyResponseElementsResponse.size()).isEqualTo(3);
    }

    @Test
    public void whenRetrievingBySurveyIdThatDoesNotExist_ShouldReturnEmptyList() {
        List<SurveyResponseElement> surveyResponseElements = this.surveyResponseElementDao.getBySurveyResponseElementBySurveyResponseId(346638267);

        assertThat(surveyResponseElements).isNotNull();
        assertThat(surveyResponseElements).isEmpty();
    }
}
