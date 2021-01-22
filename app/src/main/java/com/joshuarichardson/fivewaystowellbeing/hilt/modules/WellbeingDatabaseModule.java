package com.joshuarichardson.fivewaystowellbeing.hilt.modules;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_DATABASE_NAME;

@Module
@InstallIn(ApplicationComponent.class)
public class WellbeingDatabaseModule {
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    @Provides
    @Singleton
    public static WellbeingDatabase getWellbeingDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, WellbeingDatabase.class, WELLBEING_DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build();
        }
}
