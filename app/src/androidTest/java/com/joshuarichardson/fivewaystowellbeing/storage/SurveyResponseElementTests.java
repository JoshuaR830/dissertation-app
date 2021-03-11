package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.DatabaseInsertionHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;
import com.joshuarichardson.fivewaystowellbeing.utilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class SurveyResponseElementTests {

    private WellbeingDatabase wellbeingDb;
    private SurveyResponseDao surveyResponseDao;
    private SurveyResponseElementDao surveyResponseElementDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.surveyResponseElementDao = this.wellbeingDb.surveyResponseElementDao();
        this.surveyResponseDao = this.wellbeingDb.surveyResponseDao();
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void insertingSurveyResponseElement_ThenGettingResponseElementById_ShouldReturnTheCorrectSurveyResponse() throws TimeoutException, InterruptedException {

        long surveyId = 12345678;
        String question = "What is the question?";
        String answer = "I don't know";

        SurveyResponseElement surveyResponseElement1 = new SurveyResponseElement(surveyId, question, answer);
        SurveyResponseElement surveyResponseElement2 = new SurveyResponseElement(surveyId + 1, "what is happening?", "I don't know");

        // Inserting in this order means that the first element is not the one that the test is interested in
        surveyResponseElementDao.insert(surveyResponseElement2);
        long surveyElementId = surveyResponseElementDao.insert(surveyResponseElement1);

        List<SurveyResponseElement> surveyResponseElements = LiveDataTestUtil.getOrAwaitValue(this.surveyResponseElementDao.getSurveyResponseElementBySurveyResponseElementId(surveyElementId));

        assertThat(surveyResponseElements).isNotNull();
        assertThat(surveyResponseElements.size()).isEqualTo(1);
        SurveyResponseElement actualSurveyElement = surveyResponseElements.get(0);

        assertThat(actualSurveyElement.getId()).isEqualTo(surveyElementId);
        assertThat(actualSurveyElement.getSurveyId()).isEqualTo(surveyId);
        assertThat(actualSurveyElement.getQuestion()).isEqualTo(question);
        assertThat(actualSurveyElement.getAnswer()).isEqualTo(answer);
    }

    @Test
    public void insertingMultipleSurveyResponseElementsIndividually_ThenRetrievingBySurveyId_ShouldReturnTheCorrectNumberOfSurveys() {
        SurveyResponse surveyResponse = new SurveyResponse(6378568, WaysToWellbeing.BE_ACTIVE, "title", "description", 0, 0, 0, 0, 0);
        long surveyId = this.surveyResponseDao.insert(surveyResponse);

        SurveyResponseElement surveyResponseElement1 = new SurveyResponseElement(surveyId, "", "");
        SurveyResponseElement surveyResponseElement2 = new SurveyResponseElement(surveyId, "", "");
        SurveyResponseElement surveyResponseElement3 = new SurveyResponseElement(surveyId, "", "");
        SurveyResponseElement surveyResponseElement4 = new SurveyResponseElement(surveyId + 1, "", "");

        this.surveyResponseElementDao.insert(surveyResponseElement1);
        this.surveyResponseElementDao.insert(surveyResponseElement2);
        this.surveyResponseElementDao.insert(surveyResponseElement3);
        this.surveyResponseElementDao.insert(surveyResponseElement4);

        List<SurveyResponseElement> surveyResponseElementsResponse = null;
        try {
            surveyResponseElementsResponse = LiveDataTestUtil.getOrAwaitValue(this.surveyResponseElementDao.getSurveyResponseElementBySurveyResponseId(surveyId));
            assertThat(surveyResponseElementsResponse).isNotNull();
            assertThat(surveyResponseElementsResponse.size()).isEqualTo(3);
        } catch (TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertingMultipleSurveyResponseElementsFromAList_ThenRetrievingBySurveyId_ShouldReturnTheCorrectNumberOfSurveys() throws TimeoutException, InterruptedException {
        SurveyResponse surveyResponse = new SurveyResponse(6378568, WaysToWellbeing.BE_ACTIVE, "title", "description", 0, 0, 0, 0, 0);
        long surveyId = this.surveyResponseDao.insert(surveyResponse);

        SurveyResponseElement[] surveyResponseElements = new SurveyResponseElement[] {
                new SurveyResponseElement(surveyId, "", ""),
                new SurveyResponseElement(surveyId, "", ""),
                new SurveyResponseElement(surveyId, "", ""),
                new SurveyResponseElement(surveyId + 1, "", ""),
                new SurveyResponseElement(surveyId + 2, "", ""),
        };

        DatabaseInsertionHelper.insert(surveyResponseElements, this.surveyResponseElementDao);

        List<SurveyResponseElement> surveyResponseElementsResponse = null;

        surveyResponseElementsResponse = LiveDataTestUtil.getOrAwaitValue(this.surveyResponseElementDao.getSurveyResponseElementBySurveyResponseId(surveyId));
        assertThat(surveyResponseElementsResponse).isNotNull();
        assertThat(surveyResponseElementsResponse.size()).isEqualTo(3);

    }

    @Test
    public void whenRetrievingBySurveyIdThatDoesNotExist_ShouldReturnEmptyList() throws TimeoutException, InterruptedException {
        List<SurveyResponseElement> surveyResponseElements;

        surveyResponseElements = LiveDataTestUtil.getOrAwaitValue(this.surveyResponseElementDao.getSurveyResponseElementBySurveyResponseId(346638267));
        assertThat(surveyResponseElements).isNotNull();
        assertThat(surveyResponseElements).isEmpty();
    }
}
