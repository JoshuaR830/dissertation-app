package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.joshuarichardson.fivewaystowellbeing.CreatePassTimeActivity;
import com.joshuarichardson.fivewaystowellbeing.PassTimesAdapter;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewPassTimesFragment extends Fragment implements PassTimesAdapter.PasstimeClickListener, PassTimesAdapter.ItemCountUpdateListener {

    private RecyclerView passTimeRecycler;

    @Inject
    WellbeingDatabase db;

    private LiveData<List<ActivityRecord>> passTimes;
    private Observer<List<ActivityRecord>> passTimeObserver;
    private PassTimesAdapter passtimeAdapter;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.root = inflater.inflate(R.layout.fragment_view_pass_times, container, false);

        ActivityRecordDao passTimeDao = this.db.activityRecordDao();

        passTimes = passTimeDao.getAllActivities();

        this.passTimeRecycler = root.findViewById(R.id.passTimeRecyclerView);
        this.passTimeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        this.passtimeAdapter = new PassTimesAdapter(getContext(), new ArrayList<>(), this, this);
        this.passTimeRecycler.setAdapter(this.passtimeAdapter);

        EditText searchTextView = root.findViewById(R.id.passtime_search_box);

        passTimeObserver = passTimeData -> {
            // This updates the recycler view and filters it by the search term for better navigation
            this.passtimeAdapter.setValues(passTimeData, searchTextView.getText().toString());
        };

        passTimes.observe(getActivity(), passTimeObserver);

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ViewPassTimesFragment.this.passtimeAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button button = root.findViewById(R.id.create_from_search_button);
        button.setOnClickListener(v -> {
            onCreateFromSearchButtonClicked(v);
        });
        return this.root;
    }

    public void onCreateFromSearchButtonClicked(View v) {
        EditText searchText = this.root.findViewById(R.id.passtime_search_box);
        String searchQuery = searchText.getText().toString();
        Intent createActivityIntent = new Intent(getContext(), CreatePassTimeActivity.class);

        Bundle activityBundle = new Bundle();
        activityBundle.putString("new_activity_name", searchQuery);
        createActivityIntent.putExtras(activityBundle);

        startActivity(createActivityIntent);
    }

    @Override
    public void itemCount(int size) {
        Button button = this.root.findViewById(R.id.create_from_search_button);
        View card = this.root.findViewById(R.id.passTimeRecyclerView);
        if(size > 0) {
            button.setVisibility(View.INVISIBLE);
            card.setVisibility(View.VISIBLE);
        } else {
            card.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(View view, ActivityRecord passtime) {
        // As this is used in 2 places but only one of them has clickable recycler items - only make clicks work on ViewPassTimesActivity
        if(getActivity().getClass().getName().equals(ViewPassTimesActivity.class.getName())) {
            Intent passtimeResult = new Intent();
            Bundle passtimeBundle = new Bundle();
            passtimeBundle.putLong("activity_id", passtime.getActivityRecordId());
            passtimeBundle.putString("activity_type", passtime.getActivityType());
            passtimeBundle.putString("activity_name", passtime.getActivityName());
            passtimeBundle.putString("activity_way_to_wellbeing", passtime.getActivityWayToWellbeing());
            passtimeResult.putExtras(passtimeBundle);
            getActivity().setResult(Activity.RESULT_OK, passtimeResult);
            getActivity().finish();
        }
    }
}
