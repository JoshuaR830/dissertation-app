package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import static com.google.common.truth.Truth.assertThat;

public class UpdateSurveyActivityTests {

    private WellbeingDatabase wellbeingDb;
    private SurveyResponseActivityRecordDao surveyActivityDao;
    private long surveyActivityId;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.surveyActivityDao = wellbeingDb.surveyResponseActivityRecordDao();

        SurveyResponseDao surveyResponseDao = wellbeingDb.surveyResponseDao();
        ActivityRecordDao activityRecordDao = wellbeingDb.activityRecordDao();

        long surveyId = surveyResponseDao.insert(new SurveyResponse(1607960245, "Be active", "title", "description"));
        long activityId = activityRecordDao.insert(new ActivityRecord("Running", 1200, 1607960240, "Sport", "UNASSIGNED", false));

        this.surveyActivityId = this.surveyActivityDao.insert(new SurveyResponseActivityRecord(surveyId, activityId, 1, "note 1", 1612427791, 1612427795, 0, false));
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void updateANote_ShouldUpdateTheNote() {
        SurveyResponseActivityRecord response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getNote()).isEqualTo("note 1");

        this.surveyActivityDao.updateNote(this.surveyActivityId, "A new note");

        response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getNote()).isEqualTo("A new note");
    }

    @Test
    public void updateAStartTime_ShouldUpdateTheStartTime() {
        SurveyResponseActivityRecord response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getStartTime()).isEqualTo(1612427791);

        this.surveyActivityDao.updateStartTime(this.surveyActivityId, 1234567);

        response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getStartTime()).isEqualTo(1234567);
    }

    @Test
    public void updateAEndTime_ShouldUpdateTheEndTime() {
        SurveyResponseActivityRecord response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getEndTime()).isEqualTo(1612427795);

        this.surveyActivityDao.updateEndTime(this.surveyActivityId, 7654321);

        response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getEndTime()).isEqualTo(7654321);
    }

    @Test
    public void updateEmotion_ShouldUpdateTheEmotion() {
        SurveyResponseActivityRecord response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getEmotion()).isEqualTo(0);

        this.surveyActivityDao.updateEmotion(this.surveyActivityId, 1);

        response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getEmotion()).isEqualTo(1);
    }

    @Test
    public void updateIsDone_ShouldUpdateIsDone() {
        SurveyResponseActivityRecord response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getIsDone()).isEqualTo(false);

        this.surveyActivityDao.updateIsDone(this.surveyActivityId, true);

        response = this.surveyActivityDao.getSurveyActivityById(this.surveyActivityId);
        assertThat(response.getIsDone()).isEqualTo(true);
    }
}
