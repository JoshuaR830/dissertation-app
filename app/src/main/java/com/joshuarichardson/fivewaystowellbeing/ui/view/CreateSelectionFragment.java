package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CreateSelectionFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_progress, parentView, false);
        return root;
    }

    // ToDo query the database to see if there are any surveys today
    // ToDo - create a new query that allows to query by time
    // ToDo - if there are surveys remove existing items form the linear layout
    // ToDo - loop over each survey completed and add it to the list
}
