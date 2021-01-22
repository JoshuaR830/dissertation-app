package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joshuarichardson.fivewaystowellbeing.CreatePassTimeActivity;
import com.joshuarichardson.fivewaystowellbeing.PassTimesAdapter;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewPassTimesFragment extends Fragment {
    private RecyclerView passTimeRecycler;

    @Inject
    WellbeingDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        ActivityRecordDao passTimeDao = this.db.activityRecordDao();

        LiveData<List<ActivityRecord>> passTimes = passTimeDao.getAllActivities();

        View root = inflater.inflate(R.layout.fragment_view_pass_times, parentView, false);

        this.passTimeRecycler = root.findViewById(R.id.passTimeRecyclerView);
        this.passTimeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        Observer<List<ActivityRecord>> passTimeObserver = passTimeData -> {
            PassTimesAdapter passTimeAdapter = new PassTimesAdapter(getActivity(), passTimeData);
            this.passTimeRecycler.setAdapter(passTimeAdapter);
        };

        passTimes.observe(getViewLifecycleOwner(), passTimeObserver);


        return root;
    }

    public void onCreatePassTimeButtonClicked(View v) {
        Intent answerSurveyIntent = new Intent(getActivity(), CreatePassTimeActivity.class);
        startActivity(answerSurveyIntent);
    }
}
