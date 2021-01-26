package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.QuestionsToAskDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyQuestionSetDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes;
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

public class SurveyQuestionSetTests {
    private WellbeingDatabase wellbeingDb;
    private QuestionsToAskDao questionsToAskDao;
    private SurveyQuestionSetDao surveyQuestionSetDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.questionsToAskDao = this.wellbeingDb.questionsToAskDao();
        this.surveyQuestionSetDao = this.wellbeingDb.surveyQuestionSetDao();
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void insertSets_thenGetAllSetsWithoutSurveys_ShouldReturnAllIncompleteSurveyQuestionSets() throws TimeoutException, InterruptedException {
        SurveyQuestionSet set1 = new SurveyQuestionSet(376248736, 1);
        SurveyQuestionSet set2 = new SurveyQuestionSet(476248747, 0);
        SurveyQuestionSet set3 = new SurveyQuestionSet(576248758, 0);

        this.surveyQuestionSetDao.insert(set1);
        this.surveyQuestionSetDao.insert(set2);
        this.surveyQuestionSetDao.insert(set3);

        List<SurveyQuestionSet> retrievedSurveyQuestionSets = LiveDataTestUtil.getOrAwaitValue(this.surveyQuestionSetDao.getUnansweredSurveyQuestionSets());

        // Check that only the 2 surveyId = 0 records are returned
        assertThat(retrievedSurveyQuestionSets.size()).isEqualTo(2);

        // There should be no setId
        assertThat(retrievedSurveyQuestionSets.get(0).getSurveyId()).isEqualTo(0);
        assertThat(retrievedSurveyQuestionSets.get(1).getSurveyId()).isEqualTo(0);
    }

    @Test
    public void insertQuestionSets_thenGetQuestionSetsById_ShouldReturnCorrectSurveyQuestionSet() throws TimeoutException, InterruptedException {
        SurveyQuestionSet set1 = new SurveyQuestionSet(376248734, 1);
        SurveyQuestionSet set2 = new SurveyQuestionSet(476248745, 2);
        SurveyQuestionSet set3 = new SurveyQuestionSet(576248756, 3);

        this.surveyQuestionSetDao.insert(set1);
        long set2Id = this.surveyQuestionSetDao.insert(set2);
        this.surveyQuestionSetDao.insert(set3);


        List<SurveyQuestionSet> retrievedSurveyQuestionSets = LiveDataTestUtil.getOrAwaitValue(this.surveyQuestionSetDao.getSurveyQuestionSetById(set2Id));

        // There should only be 1 matching the id
        assertThat(retrievedSurveyQuestionSets.size()).isEqualTo(1);

        // Ensure that the returned set is correct
        SurveyQuestionSet retrievedSurveyQuestionSet = retrievedSurveyQuestionSets.get(0);
        assertThat(retrievedSurveyQuestionSet.getSurveyId()).isEqualTo(2);
        assertThat(retrievedSurveyQuestionSet.getTimestamp()).isEqualTo(476248745);
    }

    @Test
    public void insertUnansweredSet_thenUpdateItToComplete_ShouldHaveANonZeroSurveyId() throws TimeoutException, InterruptedException {
        SurveyQuestionSet set1 = new SurveyQuestionSet(476248747, 0);
        SurveyQuestionSet set2 = new SurveyQuestionSet(476248748, 0);
        long setId1 = this.surveyQuestionSetDao.insert(set1);
        long setId2 = this.surveyQuestionSetDao.insert(set2);
        this.surveyQuestionSetDao.updateSetWithCompletedSurveyId(setId1, 123);

        // Get the survey by id to check that the correct survey has been updated
        List<SurveyQuestionSet> retrievedSurveyQuestionSets1 = LiveDataTestUtil.getOrAwaitValue(this.surveyQuestionSetDao.getSurveyQuestionSetById(setId1));
        List<SurveyQuestionSet> retrievedSurveyQuestionSets2 = LiveDataTestUtil.getOrAwaitValue(this.surveyQuestionSetDao.getSurveyQuestionSetById(setId2));

        // Check that the value of the survey has been updated
        assertThat(retrievedSurveyQuestionSets1.size()).isEqualTo(1);
        assertThat(retrievedSurveyQuestionSets2.size()).isEqualTo(1);

        assertThat(retrievedSurveyQuestionSets1.get(0).getSurveyId()).isEqualTo(123);
        assertThat(retrievedSurveyQuestionSets2.get(0).getSurveyId()).isEqualTo(0);
    }

    @Test
    public void insertQuestionData_thenGetAllQuestionsForSpecificSurveyQuestionSet_ShouldReturnAllQuestionsForSetInAscendingSequenceOrder() throws TimeoutException, InterruptedException {

        SurveyQuestionSet set1 = new SurveyQuestionSet(376248736, 1);
        SurveyQuestionSet set2 = new SurveyQuestionSet(476248747, 0);
        SurveyQuestionSet set3 = new SurveyQuestionSet(576248758, 0);

        long set1Id = this.surveyQuestionSetDao.insert(set1);
        long set2Id = this.surveyQuestionSetDao.insert(set2);
        long set3Id = this.surveyQuestionSetDao.insert(set3);

        QuestionsToAsk question1 = new QuestionsToAsk("How was Google Chrome today?", "reason 1", set1Id, SurveyItemTypes.TEXT.name(), 0, null);
        QuestionsToAsk question2 = new QuestionsToAsk("How was Reddit today?", "reason 2", set2Id, SurveyItemTypes.DROP_DOWN_LIST.name(), 1, null);
        QuestionsToAsk question3 = new QuestionsToAsk("How was Instagram today?", "reason 3", set3Id, SurveyItemTypes.DROP_DOWN_LIST.name(), 1, null);
        QuestionsToAsk question4 = new QuestionsToAsk("How was YouTube today?", "reason 4", set2Id, SurveyItemTypes.TEXT.name(), 0, null);

        this.questionsToAskDao.insert(question1);
        long question2Id = this.questionsToAskDao.insert(question2);
        this.questionsToAskDao.insert(question3);
        long question4Id = this.questionsToAskDao.insert(question4);

        List<QuestionsToAsk> retrievedQuestionSets = LiveDataTestUtil.getOrAwaitValue(this.surveyQuestionSetDao.getQuestionsForSurveyQuestionSetWithId(set2Id));
        assertThat(retrievedQuestionSets.size()).isEqualTo(2);

        QuestionsToAsk firstQuestion = retrievedQuestionSets.get(0);
        QuestionsToAsk secondQuestion = retrievedQuestionSets.get(1);

        assertThat(firstQuestion.getSetId()).isEqualTo(set2Id);
        assertThat(firstQuestion.getType()).isEqualTo("TEXT");
        assertThat(firstQuestion.getSequenceNumber()).isEqualTo(0);
        assertThat(firstQuestion.getReason()).isEqualTo("reason 4");
        assertThat(firstQuestion.getQuestion()).isEqualTo("How was YouTube today?");
        assertThat(firstQuestion.getId()).isEqualTo(question4Id);

        assertThat(secondQuestion.getSetId()).isEqualTo(set2Id);
        assertThat(secondQuestion.getType()).isEqualTo("DROP_DOWN_LIST");
        assertThat(secondQuestion.getSequenceNumber()).isEqualTo(1);
        assertThat(secondQuestion.getReason()).isEqualTo("reason 2");
        assertThat(secondQuestion.getQuestion()).isEqualTo("How was Reddit today?");
        assertThat(secondQuestion.getId()).isEqualTo(question2Id);
    }
}
