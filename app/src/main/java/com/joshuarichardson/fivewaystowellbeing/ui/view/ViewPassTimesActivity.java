package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewPassTimesActivity extends AppCompatActivity implements PassTimesAdapter.PasstimeClickListener, PassTimesAdapter.ItemCountUpdateListener {

    private RecyclerView passTimeRecycler;

    @Inject
    WellbeingDatabase db;

    private LiveData<List<ActivityRecord>> passTimes;
    private Observer<List<ActivityRecord>> passTimeObserver;
    private PassTimesAdapter passtimeAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pass_times);
        ActivityRecordDao passTimeDao = this.db.activityRecordDao();

        passTimes = passTimeDao.getAllActivities();

        this.passTimeRecycler = findViewById(R.id.passTimeRecyclerView);
        this.passTimeRecycler.setLayoutManager(new LinearLayoutManager(this));

        this.passtimeAdapter = new PassTimesAdapter(this, new ArrayList<>(), this, this);
        this.passTimeRecycler.setAdapter(this.passtimeAdapter);

        EditText searchTextView = findViewById(R.id.passtime_search_box);

        passTimeObserver = passTimeData -> {
            // This updates the recycler view and filters it by the search term for better navigation
            this.passtimeAdapter.setValues(passTimeData, searchTextView.getText().toString());
        };

        passTimes.observe(this, passTimeObserver);

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ViewPassTimesActivity.this.passtimeAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void onCreateFromSearchButtonClicked(View v) {
        EditText searchText = findViewById(R.id.passtime_search_box);
        String searchQuery = searchText.getText().toString();
        Intent createActivityIntent = new Intent(this, CreatePassTimeActivity.class);

        Bundle activityBundle = new Bundle();
        activityBundle.putString("new_activity_name", searchQuery);
        createActivityIntent.putExtras(activityBundle);

        startActivity(createActivityIntent);
    }

    public void onCreatePassTimeButtonClicked(View v) {
        Intent answerSurveyIntent = new Intent(ViewPassTimesActivity.this, CreatePassTimeActivity.class);
        startActivity(answerSurveyIntent);
    }

    @Override
    public void onItemClick(View view, ActivityRecord passtime) {
        Intent passtimeResult = new Intent();
        Bundle passtimeBundle = new Bundle();
        passtimeBundle.putLong("activity_id", passtime.getActivityRecordId());
        passtimeBundle.putString("activity_type", passtime.getActivityType());
        passtimeBundle.putString("activity_name", passtime.getActivityName());
        passtimeResult.putExtras(passtimeBundle);
        setResult(Activity.RESULT_OK, passtimeResult);
        finish();
    }

    @Override
    public void itemCount(int size) {
        Button button = findViewById(R.id.create_from_search_button);
        View card = findViewById(R.id.recycler_card_view);
        if(size > 0) {
            button.setVisibility(View.INVISIBLE);
            card.setVisibility(View.VISIBLE);
        } else {
            card.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
        }
    }
}
