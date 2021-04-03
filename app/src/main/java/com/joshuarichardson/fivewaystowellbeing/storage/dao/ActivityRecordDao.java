package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ActivityRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ActivityRecord activityRecord);

    @Query("SELECT * FROM activity_records WHERE id = :activityRecordId")
    ActivityRecord getActivityRecordById(long activityRecordId);

    @Query("SELECT * FROM activity_records WHERE timestamp >= :startTime AND timestamp <= :endTime")
    List<ActivityRecord> getActivitiesInTimeRange(long startTime, long endTime);

    @Query("SELECT * FROM activity_records WHERE is_hidden = 0")
    LiveData<List<ActivityRecord>> getAllActivities();

    @Query("SELECT * FROM activity_records WHERE is_hidden = 0")
    List<ActivityRecord> getAllVisibleActivitiesNotLive();

    @Query("SELECT * FROM activity_records")
    List<ActivityRecord> getAllActivitiesNotLive();

    // ToDo - consider deleting this query
    @Query("SELECT * FROM activity_records WHERE name LIKE :searchTerm || '%' ")
    List<ActivityRecord> getActivitiesMatchingSearch(String searchTerm);

    @Query("UPDATE activity_records SET type = :type, name = :name, way_to_wellbeing = :wayToWellbeingString, timestamp = :timestamp WHERE id = :activityRecordId")
    void update(long activityRecordId, String name, String type, String wayToWellbeingString, long timestamp);

    @Query("UPDATE activity_records SET is_hidden = :isHidden WHERE id = :activityRecordId")
    void flagHidden(long activityRecordId, boolean isHidden);

    // ToDo Delete may be necessary later
}
