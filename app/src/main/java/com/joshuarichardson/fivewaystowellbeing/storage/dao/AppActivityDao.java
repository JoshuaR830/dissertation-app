package com.joshuarichardson.fivewaystowellbeing.storage.dao;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.AppActivity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AppActivityDao {

    @Insert
    void insert(AppActivity appActivity);

    @Query("SELECT * FROM app_usage_table WHERE package_id = :packageName")
    AppActivity getPhysicalActivityByType(String packageName);

    @Query("SELECT * FROM app_usage_table WHERE package_id = :packageName AND is_pending = 0")
    AppActivity getAppActivityByTypeNotPending(String packageName);

    @Query("UPDATE app_usage_table SET is_pending = :pendingStatus WHERE package_id = :packageName AND is_pending = 0")
    void setPending(String packageName, boolean pendingStatus);

    @Query("UPDATE app_usage_table SET start_time = :startTime, current_usage = :timeInForeground WHERE package_id = :packageName AND is_pending = 0")
    void updateStartAndUsageTime(String packageName, long startTime, long timeInForeground);
}
