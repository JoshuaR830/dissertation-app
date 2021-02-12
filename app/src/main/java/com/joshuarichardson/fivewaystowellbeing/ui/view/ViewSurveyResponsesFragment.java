package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.SurveyResponseAdapter;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewSurveyResponsesFragment extends Fragment {
    @Inject
    WellbeingDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_surveys, parentView, false);

        RecyclerView recycler = root.findViewById(R.id.surveyRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        SurveyResponseDao surveyResponseDao = this.db.surveyResponseDao();

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<SurveyResponse> historyPageData = surveyResponseDao.getHistoryPageData();
            ArrayList<HistoryPageData> historyList = new ArrayList<>();

            Calendar morning = new GregorianCalendar();
            Calendar night = new GregorianCalendar();
            for(SurveyResponse pageItem : historyPageData) {
                morning.setTime(new Date(pageItem.getSurveyResponseTimestamp()));
                night.setTime(new Date(pageItem.getSurveyResponseTimestamp()));
                morning.set(Calendar.HOUR_OF_DAY, 0);
                morning.set(Calendar.MINUTE, 0);
                morning.set(Calendar.SECOND, 0);
                morning.set(Calendar.MILLISECOND, 0);

                night.set(Calendar.HOUR_OF_DAY, 23);
                night.set(Calendar.MINUTE, 59);
                night.set(Calendar.SECOND, 59);
                night.set(Calendar.MILLISECOND, 999);

                List<WellbeingGraphItem> graphItems = this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimesNotLive(morning.getTimeInMillis(), night.getTimeInMillis());
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
