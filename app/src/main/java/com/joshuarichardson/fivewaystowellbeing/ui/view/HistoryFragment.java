package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistoryFragment extends Fragment {
    @Inject
    WellbeingDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recycler = root.findViewById(R.id.surveyRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        SurveyResponseDao surveyResponseDao = this.db.surveyResponseDao();

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<SurveyResponse> historyPageData = surveyResponseDao.getHistoryPageData();
            ArrayList<HistoryPageData> historyList = new ArrayList<>();
            
            for(SurveyResponse pageItem : historyPageData) {
                Date now = new Date(pageItem.getSurveyResponseTimestamp());
                long morning = TimeHelper.getStartOfDay(now.getTime());
                long night = TimeHelper.getEndOfDay(now.getTime());

                List<WellbeingGraphItem> graphItems = this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimesNotLive(morning, night);
                WellbeingGraphValueHelper wellbeingGraphValues = WellbeingGraphValueHelper.getWellbeingGraphValues(graphItems);
                historyList.add(new HistoryPageData(pageItem, wellbeingGraphValues));
            }

            requireActivity().runOnUiThread(() -> {
                SurveyResponseAdapter adapter = new SurveyResponseAdapter(getActivity(), historyList);
                recycler.setAdapter(adapter);
            });

        });

        return root;
    }
}
