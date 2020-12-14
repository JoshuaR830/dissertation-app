package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.SurveyResponseAdapter;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewSurveyResponsesFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_surveys, parentView, false);

        RecyclerView recycler = root.findViewById(R.id.surveyRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        WellbeingDatabase db = WellbeingDatabase.getWellbeingDatabase(getActivity());
        SurveyResponseDao surveyResponseDao = db.surveyResponseDao();

        Observer<List<SurveyResponse>> responseObserver = surveyResponses -> {
            SurveyResponseAdapter adapter = new SurveyResponseAdapter(getActivity(), surveyResponses);
            recycler.setAdapter(adapter);
        };

        surveyResponseDao.getAllSurveyResponses().observe(getViewLifecycleOwner(), responseObserver);

        return root;
    }
}
