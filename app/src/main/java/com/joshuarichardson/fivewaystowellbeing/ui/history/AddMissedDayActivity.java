package com.joshuarichardson.fivewaystowellbeing.ui.history;

import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * An activity that displays a recycler view of all the days that a user has missed logging
 */
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

        // Observe changes to the history data so that it updates in real time
        Observer<List<SurveyResponse>> historyObserver = historyPageData -> {

            ArrayList<HistoryPageData> historyList = new ArrayList<>();

            WellbeingDatabaseModule.databaseExecutor.execute(() -> {
                for(SurveyResponse pageItem : historyPageData) {
                    long now = pageItem.getSurveyResponseTimestamp();
                    long morning = TimeHelper.getStartOfDay(now);
                    long night = TimeHelper.getEndOfDay(now);

                    // Get the graph for each item in the list
                    List<WellbeingGraphItem> graphItems = this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimesNotLive(morning, night);
                    WellbeingGraphValueHelper wellbeingGraphValues = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItems);
                    historyList.add(new HistoryPageData(pageItem, wellbeingGraphValues));
                }

                runOnUiThread(() -> {
                    // Add items to the recycler view
                    adapter.setValues(historyList);
                });
            });
        };

        // Observe the empty values
        surveyResponseDao.getEmptyHistoryPageData().observe(this, historyObserver);
    }
}