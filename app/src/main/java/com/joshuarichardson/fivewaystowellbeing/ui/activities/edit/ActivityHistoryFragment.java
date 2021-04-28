package com.joshuarichardson.fivewaystowellbeing.ui.activities.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
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
public class ActivityHistoryFragment extends Fragment implements ActivitiesAdapter.ActivityClickListener, ActivitiesAdapter.ItemCountUpdateListener {

    private RecyclerView activityRecycler;

    @Inject
    WellbeingDatabase db;

    private LiveData<List<ActivityRecord>> activities;
    private Observer<List<ActivityRecord>> activityObserver;
    private ActivitiesAdapter activityAdapter;
    private View root;
    private boolean isEditable;
    private boolean isEdited;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_view_activities, container, false);

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<ActivityRecord> oldActivities = db.activityRecordDao().getActivitiesInTimeRange(0, 1613509560000L);

            // Only show the button if there are old items
            boolean shouldShowSnackbar = false;
            for(ActivityRecord activity : oldActivities) {
                if(activity.getActivityWayToWellbeing().equals(WaysToWellbeing.UNASSIGNED.toString()) && !activity.getIsHidden()) {
                    shouldShowSnackbar = true;
                    break;
                }
            }

            if(shouldShowSnackbar) {
                requireActivity().runOnUiThread(() -> {
                    // Reference https://developer.android.com/training/snackbar/showing#java
                    Snackbar snackbar = Snackbar.make(root, getText(R.string.old_activity_warning_snackbar), 10000)
                        .setAnchorView(root.findViewById(R.id.create_activity_button))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setAction(R.string.button_edit, v -> {
                            makeEditable();
                        });
                    snackbar.show();
                });
            }
        });

        ActivityRecordDao activityDao = this.db.activityRecordDao();

        activities = activityDao.getAllActivities();

        this.activityRecycler = root.findViewById(R.id.activity_recycler_view);
        this.activityRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        this.activityAdapter = new ActivitiesAdapter(getContext(), new ArrayList<>(), this, this);
        this.activityRecycler.setAdapter(this.activityAdapter);

        EditText searchTextView = root.findViewById(R.id.activity_search_box);

        activityObserver = ActivityData -> {
            // This updates the recycler view and filters it by the search term for better navigation
            this.activityAdapter.setValues(ActivityData, searchTextView.getText().toString());
        };

        activities.observe(getActivity(), activityObserver);

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ActivityHistoryFragment.this.activityAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Button button = root.findViewById(R.id.create_from_search_button);
        button.setOnClickListener(v -> {
            onCreateFromSearchButtonClicked(v);
        });

        // Reference https://stackoverflow.com/a/47531110/13496270
        setHasOptionsMenu(true);

        return this.root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onCreateFromSearchButtonClicked(View v) {
        EditText searchText = this.root.findViewById(R.id.activity_search_box);
        String searchQuery = searchText.getText().toString();
        Intent createActivityIntent = new Intent(getContext(), CreateOrUpdateActivityActivity.class);

        Bundle activityBundle = new Bundle();
        activityBundle.putString("new_activity_name", searchQuery);
        createActivityIntent.putExtras(activityBundle);

        startActivity(createActivityIntent);
    }

    @Override
    public void itemCount(int size) {
        Button button = this.root.findViewById(R.id.create_from_search_button);
        View card = this.root.findViewById(R.id.activity_recycler_view);
        if(size > 0) {
            button.setVisibility(View.INVISIBLE);
            card.setVisibility(View.VISIBLE);
        } else {
            card.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(View view, ActivityRecord activity) {
        // As this is used in 2 places but only one of them has clickable recycler items - only make clicks work on ViewActivitiesActivity
        if(getActivity().getClass().getName().equals(ViewActivitiesActivity.class.getName())) {
            Intent activityResult = new Intent();
            Bundle activityBundle = new Bundle();
            activityBundle.putLong("activity_id", activity.getActivityRecordId());
            activityBundle.putString("activity_type", activity.getActivityType());
            activityBundle.putString("activity_name", activity.getActivityName());
            activityBundle.putString("activity_way_to_wellbeing", activity.getActivityWayToWellbeing());
            activityBundle.putBoolean("is_edited", this.isEdited);
            activityResult.putExtras(activityBundle);
            getActivity().setResult(Activity.RESULT_OK, activityResult);
            getActivity().finish();
        }
    }

    @Override
    public void onEditClick(View view, ActivityRecord activity) {

        this.isEdited = true;
        // Launch activity to update a Activity
        Intent editActivityIntent = new Intent(getActivity(), CreateOrUpdateActivityActivity.class);
        Bundle editBundle = new Bundle();
        editBundle.putLong("activity_id", activity.getActivityRecordId());
        editBundle.putString("activity_name", activity.getActivityName());
        editBundle.putString("activity_type", activity.getActivityType());
        editBundle.putString("activity_way_to_wellbeing", activity.getActivityWayToWellbeing());
        editActivityIntent.putExtras(editBundle);
        startActivity(editActivityIntent);
    }

    @Override
    public void onDeleteClick(View view, ActivityRecord activity) {
        new MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.title_delete_activity)
            .setMessage(R.string.body_delete_activity)
            .setIcon(R.drawable.icon_close)
            .setPositiveButton(R.string.button_delete, (dialog, which) -> {
                // Set the pass time to hidden
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    this.db.activityRecordDao().flagHidden(activity.getActivityRecordId(), true);
                });
            })
            .setNegativeButton(R.string.button_cancel, (dialog, which) -> {})
            .create()
            .show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    // Reference: https://stackoverflow.com/a/47531110/13496270
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_edit).setVisible(true);
    }

    public void makeEditable() {
        if (this.isEditable) {
            this.isEditable = false;
        } else {
            this.isEditable = true;
        }
        // This updates the recycler view and filters it by the search term for better navigation
        this.activityAdapter.editableList(this.isEditable);
    }
}
