package com.joshuarichardson.fivewaystowellbeing.hilt.modules;

import android.content.Context;
import android.util.Log;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.QuestionsToAsk;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyQuestionSet;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_DATABASE_NAME;
import static com.joshuarichardson.fivewaystowellbeing.surveys.SurveyItemTypes.BASIC_SURVEY;

@Module
@InstallIn(ApplicationComponent.class)
public class WellbeingDatabaseModule {
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    @Provides
    @Singleton
    public static WellbeingDatabase getWellbeingDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, WellbeingDatabase.class, WELLBEING_DATABASE_NAME)
            .addCallback(new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    databaseWriteExecutor.execute(() -> {
                        Date now = new Date();
                        long setId = getWellbeingDatabase(context).surveyQuestionSetDao().insert(new SurveyQuestionSet(now.getTime(), 0));
                        getWellbeingDatabase(context).questionsToAskDao().insert(new QuestionsToAsk("", "", setId, BASIC_SURVEY.toString(), 0, null));
                    });
                }

                @Override
                public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                    super.onDestructiveMigration(db);
                    databaseWriteExecutor.execute(() -> {
                        Date now = new Date();
                        Log.d("Destructive migration", "Add the stuff");
                        long setId = getWellbeingDatabase(context).surveyQuestionSetDao().insert(new SurveyQuestionSet(now.getTime(), 0));
                        Log.d("Set Id", String.valueOf(setId));
                        getWellbeingDatabase(context).questionsToAskDao().insert(new QuestionsToAsk("", "", setId, BASIC_SURVEY.toString(), 0, null));
                    });
                }
            })
            .fallbackToDestructiveMigration()
            .build();
        }
}
