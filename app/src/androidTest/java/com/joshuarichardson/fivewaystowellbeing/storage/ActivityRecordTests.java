package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
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
    public void insertActivity_ThenGetById_ShouldReturnTheCorrectActivity() {
        ActivityRecord insertedActivity = new ActivityRecord("Running", 1200, 1607960240, ActivityType.SPORT);
        int activityRecordId = (int) this.activityDao.insert(insertedActivity);
        insertedActivity.setActivityRecordId(activityRecordId);

        List<ActivityRecord> retrievedActivities = this.activityDao.getActivityRecordById(activityRecordId);

        assertThat(retrievedActivities.size()).isGreaterThan(0);

        ActivityRecord actualActivity = retrievedActivities.get(0);

        assertThat(actualActivity.getActivityType()).isEqualTo(ActivityType.SPORT.name());
        assertThat(actualActivity.getActivityName()).isEqualTo("Running");
        assertThat(actualActivity.getActivityRecordId()).isEqualTo(activityRecordId);
        assertThat(actualActivity.getActivityDuration()).isEqualTo(1200);
        assertThat(actualActivity.getActivityTimestamp()).isEqualTo(1607960240);
    }
}
