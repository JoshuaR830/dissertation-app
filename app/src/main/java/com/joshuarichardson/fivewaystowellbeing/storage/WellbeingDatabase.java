package com.joshuarichardson.fivewaystowellbeing.storage;

import android.content.Context;

import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseElementDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import static com.joshuarichardson.fivewaystowellbeing.storage.WaysToWellbeingContract.WELLBEING_DATABASE_NAME;

@Database(entities = {ActivityRecord.class, SurveyResponse.class, SurveyResponseActivityRecord.class, SurveyResponseElement.class}, exportSchema = false, version = 1)
public abstract class WellbeingDatabase extends RoomDatabase {

    private static WellbeingDatabase WELLBEING_DATABASE_INSTANCE;

    public abstract ActivityRecordDao activityRecordDao();
    public abstract SurveyResponseActivityRecordDao surveyResponseActivityRecordDao();
    public abstract SurveyResponseDao surveyResponseDao();
    public abstract SurveyResponseElementDao surveyResponseElementDao();

    public static WellbeingDatabase getWellbeingDatabase(Context context) {
        if(WELLBEING_DATABASE_INSTANCE == null) {

            // ToDo: need to find out what synchronised does

            WELLBEING_DATABASE_INSTANCE = Room.databaseBuilder(context.getApplicationContext(), WellbeingDatabase.class, WELLBEING_DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
        }

        return WELLBEING_DATABASE_INSTANCE;
    }

}
