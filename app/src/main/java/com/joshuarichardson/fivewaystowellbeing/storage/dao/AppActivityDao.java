package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.AppActivity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AppActivityDao {

    @Insert
    void insert(AppActivity appActivity);

    @Query("SELECT * FROM app_usage_table WHERE package_id = :packageName")
    AppActivity getPhysicalActivityByType(String packageName);

    @Query("UPDATE app_usage_table SET is_pending = :pendingStatus WHERE package_id = :packageName AND is_pending = 0")
    void setPending(String packageName, boolean pendingStatus);

    @Query("UPDATE app_usage_table SET start_time = :startTime, most_recent_resume_time = :timeInForeground WHERE package_id = :packageName AND is_pending = 0")
    void updateStartAndLastResumedTime(String packageName, long startTime, long timeInForeground);

    @Query("UPDATE app_usage_table SET start_time = :timeStamp WHERE package_id = :packageName AND is_pending = 0")
    void updateStartTime(String packageName, long timeStamp);

    @Query("UPDATE app_usage_table SET end_time = :timeStamp WHERE package_id = :packageName AND is_pending = 0")
    void updateEndTime(String packageName, long timeStamp);

    @Query("UPDATE app_usage_table SET most_recent_resume_time = :lastStart WHERE package_id = :packageName AND is_pending = 0")
    void updateUsageTime(String packageName, long lastStart);

    @Query("SELECT * FROM app_usage_table WHERE end_time > most_recent_resume_time AND end_time < :durationDifference")
    List<AppActivity> getFinishedActivities(long durationDifference);

    @Query("DELETE FROM app_usage_table WHERE package_id = :packageId")
    void deleteWithPackageId(String packageId);

    @Query("DELETE FROM app_usage_table")
    void deleteAll();
}