package com.joshuarichardson.fivewaystowellbeing.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joshuarichardson.fivewaystowellbeing.R;
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
import androidx.lifecycle.Observer;
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
        SurveyResponseAdapter adapter = new SurveyResponseAdapter(getActivity(), new ArrayList<>());
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

                requireActivity().runOnUiThread(() -> {
                    adapter.setValues(historyList);
                });
            });
        };

        surveyResponseDao.getNonEmptyHistoryPageData().observe(getViewLifecycleOwner(), historyObserver);

        // Reference https://stackoverflow.com/a/47531110/13496270
        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    // Reference: https://stackoverflow.com/a/47531110/13496270
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_missing_day).setVisible(true);
    }
}
