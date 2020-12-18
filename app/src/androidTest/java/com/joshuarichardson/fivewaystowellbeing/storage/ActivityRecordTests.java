package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.DatabaseInsertionHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.utilities.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ActivityRecordTests {


    private WellbeingDatabase wellbeingDb;
    private ActivityRecordDao activityDao;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // https://developer.android.com/training/data-storage/room/testing-db
        Context context = ApplicationProvider.getApplicationContext();
        this.wellbeingDb = Room.inMemoryDatabaseBuilder(context, WellbeingDatabase.class).build();
        this.activityDao = this.wellbeingDb.activityRecordDao();
    }

    @After
    public void teardown() {
        this.wellbeingDb.close();
    }

    @Test
    public void insertActivity_ThenGetById_ShouldReturnTheCorrectActivity() throws TimeoutException, InterruptedException {
        ActivityRecord insertedActivity = new ActivityRecord("Running", 1200, 1607960240, ActivityType.SPORT);
        int activityRecordId = (int) this.activityDao.insert(insertedActivity);
        insertedActivity.setActivityRecordId(activityRecordId);

        List<ActivityRecord> retrievedActivities = LiveDataTestUtil.getOrAwaitValue(this.activityDao.getActivityRecordById(activityRecordId));

        assertThat(retrievedActivities.size()).isGreaterThan(0);

        ActivityRecord actualActivity = retrievedActivities.get(0);

        assertThat(actualActivity.getActivityType()).isEqualTo(ActivityType.SPORT.name());
        assertThat(actualActivity.getActivityName()).isEqualTo("Running");
        assertThat(actualActivity.getActivityRecordId()).isEqualTo(activityRecordId);
        assertThat(actualActivity.getActivityDuration()).isEqualTo(1200);
        assertThat(actualActivity.getActivityTimestamp()).isEqualTo(1607960240);
    }

    @Test
    public void insertMultiple_ThenGetActivities_ShouldReturnAllActivities() throws TimeoutException, InterruptedException {

        // Test inserting multiple activity records
        ArrayList<Integer> insertedIds = DatabaseInsertionHelper.insert(new ActivityRecord[]{
            new ActivityRecord("Jumping", 1201, 1607960241, ActivityType.SPORT),
            new ActivityRecord("Swimming", 1202, 1607960242, ActivityType.SPORT),
            new ActivityRecord("Throwing", 1203, 1607960243, ActivityType.SPORT)
        }, this.activityDao);

        assertThat(insertedIds.size())
                .isEqualTo(3);

        LiveData<List<ActivityRecord>> activities = this.activityDao.getAllActivities();

        // Check that multiple items are returned - should be 3 or more depending on order of test runs
        LiveDataTestUtil.getOrAwaitValue(activities);
        assertThat(activities.getValue()).isNotNull();
        assertThat(activities.getValue().size()).isGreaterThan(2);
    }

    @Test
    public void GetActivitiesInTimestampRange_ShouldOnlyReturnActivitiesBetweenSpecifiedTimestamps() throws TimeoutException, InterruptedException {
        ArrayList<Integer> insertIds = DatabaseInsertionHelper.insert(new ActivityRecord[]{
                // 16/12/2020 12am = 1608076800
                // 17/12/2020 12am = 1608163200
                new ActivityRecord("Snapchat", 1201, 1608076799, ActivityType.APP), // Should not be included
                new ActivityRecord("Google Photos", 1202, 1608076800, ActivityType.APP), // Should be included
                new ActivityRecord("Phone", 1203, 1608076801, ActivityType.APP), // Should be included
                new ActivityRecord("Facebook", 1204, 1608163199, ActivityType.APP), // Should be included
                new ActivityRecord("Forest", 1204, 1608163200, ActivityType.APP), // Should be included
                new ActivityRecord("Fishing", 1205, 1608163201, ActivityType.SPORT), // Should not be included
                new ActivityRecord("Forest", 1204, 0, ActivityType.APP), // Should not be included (min)
                new ActivityRecord("Play Store", 1204, 2147483647, ActivityType.APP), // Should not be included (max)
        }, this.activityDao);

        LiveData<List<ActivityRecord>> activities = this.activityDao.getActivitiesInTimeRange(1608076800, 1608163200);

        List<ActivityRecord> value = LiveDataTestUtil.getOrAwaitValue(activities);
        assertThat(value).isNotNull();
        assertThat(value.size()).isEqualTo(4);
    }
}
