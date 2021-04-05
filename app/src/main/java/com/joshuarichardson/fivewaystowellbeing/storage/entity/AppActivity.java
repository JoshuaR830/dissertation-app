package com.joshuarichardson.fivewaystowellbeing.storage.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_CURRENT_USAGE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_IS_PENDING;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_PACKAGE_ID;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_PREVIOUS_USAGE;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_START_TIME;
import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.APP_USAGE_TABLE_NAME;

@Entity(tableName = APP_USAGE_TABLE_NAME)
public class AppActivity {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = APP_USAGE_ID)
    private long id;

    @ColumnInfo(name = APP_USAGE_START_TIME)
    private long startTime;

    @ColumnInfo(name = APP_USAGE_PREVIOUS_USAGE)
    private long previousUsageTime;

    @NonNull
    @ColumnInfo(name = APP_USAGE_PACKAGE_ID)
    private String packageName;

    @ColumnInfo(name = APP_USAGE_IS_PENDING)
    private boolean isPending;

    @ColumnInfo(name = APP_USAGE_CURRENT_USAGE)
    private long currentUsage;

    public AppActivity(String packageName, long startTime, long previousUsageTime, long currentUsage, boolean isPending) {
        this.packageName = packageName;
        this.startTime = startTime;
        this.previousUsageTime = previousUsageTime;
        this.isPending = isPending;
        this.currentUsage = currentUsage;
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

    public long getPreviousUsageTime() {
        return this.previousUsageTime;
    }

    public boolean getIsPending() {
        return this.isPending;
    }

    @NonNull
    public String getPackageName() {
        return this.packageName;
    }

    public long getCurrentUsage() {
        return this.currentUsage;
    }
}
