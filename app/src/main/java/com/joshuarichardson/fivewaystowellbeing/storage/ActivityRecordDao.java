package com.joshuarichardson.fivewaystowellbeing.storage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ActivityRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ActivityRecord activityRecord);

    @Query("SELECT * FROM activity_records WHERE activity_record_id = :activityRecordId")
    List<ActivityRecord> getActivityRecordById(int activityRecordId);
//    @Query("SELECT * FROM activity_records")
//    ActivityRecord getActivityRecordById();
}
