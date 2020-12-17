package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ActivityRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ActivityRecord activityRecord);

    @Query("SELECT * FROM activity_records WHERE id = :activityRecordId")
    List<ActivityRecord> getActivityRecordById(int activityRecordId);

    // Delete may be necessary later
}
