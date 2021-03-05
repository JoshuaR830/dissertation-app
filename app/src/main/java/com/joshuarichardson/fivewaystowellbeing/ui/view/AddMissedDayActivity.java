package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.SurveyResponseAdapter;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddMissedDayActivity extends AppCompatActivity {

    @Inject
    WellbeingDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_missed_day);

        RecyclerView recycler = findViewById(R.id.missing_item_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        SurveyResponseDao surveyResponseDao = this.db.surveyResponseDao();
        SurveyResponseAdapter adapter = new SurveyResponseAdapter(this, new ArrayList<>());
        recycler.setAdapter(adapter);

        Observer<List<SurveyResponse>> historyObserver = historyPageData -> {

            ArrayList<HistoryPageData> historyList = new ArrayList<>();

            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                for(SurveyResponse pageItem : historyPageData) {
                    Date now = new Date(pageItem.getSurveyResponseTimestamp());
                    long morning = TimeHelper.getStartOfDay(now.getTime());
                    long night = TimeHelper.getEndOfDay(now.getTime());

                    List<WellbeingGraphItem> graphItems = this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimesNotLive(morning, night);
                    WellbeingGraphValueHelper wellbeingGraphValues = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItems);
                    historyList.add(new HistoryPageData(pageItem, wellbeingGraphValues));
                }

                runOnUiThread(() -> {
                    adapter.setValues(historyList);
                });
            });
        };

        surveyResponseDao.getEmptyHistoryPageData().observe(this, historyObserver);
    }
}