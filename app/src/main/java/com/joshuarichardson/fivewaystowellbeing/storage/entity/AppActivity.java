package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_MOST_RECENT_RESUME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_END_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_IS_PENDING;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_PACKAGE_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_START_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_TABLE_NAME;
/**
 * The entity representing the app activities
 * The table is used to keep a reference to running apps and the start/end times for calcualting
 */
@Entity(tableName = APP_USAGE_TABLE_NAME)
public class AppActivity {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = APP_USAGE_ID)
    private long id;

    @ColumnInfo(name = APP_USAGE_START_TIME)
    private long startTime;

    @ColumnInfo(name = APP_USAGE_END_TIME)
    private long endTime;

    @NonNull
    @ColumnInfo(name = APP_USAGE_PACKAGE_ID)
    private String packageName;

    @ColumnInfo(name = APP_USAGE_IS_PENDING)
    private boolean isPending;

    @ColumnInfo(name = APP_USAGE_MOST_RECENT_RESUME)
    private long mostRecentResumeTime;

    public AppActivity(String packageName, long startTime, long endTime, long mostRecentResumeTime, boolean isPending) {
        this.packageName = packageName;
        this.startTime = startTime;
        this.isPending = isPending;
        this.mostRecentResumeTime = mostRecentResumeTime;
        this.endTime = endTime;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    @NonNull
    public long getId() {
        return this.id;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public boolean getIsPending() {
        return this.isPending;
    }

    @NonNull
    public String getPackageName() {
        return this.packageName;
    }

    public long getMostRecentResumeTime() {
        return this.mostRecentResumeTime;
    }

    public long getEndTime() {
        return this.endTime;
    }
}
