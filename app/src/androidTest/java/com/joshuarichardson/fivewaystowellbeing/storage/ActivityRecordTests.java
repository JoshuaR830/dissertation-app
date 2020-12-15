package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ActivityRecordTests {

    // So I want to unit test the room implementation
    // Need to get the data access object in a before - so that I have access to room
    // Need to test inserting a value
    // Need to retrieve a value from it

    // The implementation can have testmode stuff allowing for in memory testing - no actual data
    //     inMemoryDatabaseBuilder
    // This allows you to test every query

    // survey_response_element
    // survey_response
    // mood_record
    // activity_record

    private WellbeingDatabase wellbeingDb;
    private ActivityRecordDao activityDao;

    @Before
    public void setup() {
        // https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.activityDao = this.wellbeingDb.activityRecordDao();
    }

    @Test
    public void insertActivityAndGetByIdShouldReturnTheCorrectActivity() {
        // ToDo should have an activity type enum
        ActivityRecord insertedActivity = new ActivityRecord("Running", 1200, 1607960240, "Sport");
        insertedActivity.setActivityType("Running");
        int activityRecordId = (int) this.activityDao.insert(insertedActivity);
        insertedActivity.setActivityRecordId(activityRecordId);

        List<ActivityRecord> retrievedActivities = this.activityDao.getActivityRecordById(activityRecordId);

        assertThat(retrievedActivities.size()).isGreaterThan(0);

        ActivityRecord actualActivity = retrievedActivities.get(0);

        // ToDo create an activity type enum
        assertThat(actualActivity.getActivityType()).isEqualTo("Running");
        assertThat(actualActivity.getActivityRecordId()).isEqualTo(activityRecordId);
        assertThat(actualActivity.getActivityDuration()).isEqualTo(1200);
        assertThat(actualActivity.getActivityTimestamp()).isEqualTo(1607960240);
    }
}
