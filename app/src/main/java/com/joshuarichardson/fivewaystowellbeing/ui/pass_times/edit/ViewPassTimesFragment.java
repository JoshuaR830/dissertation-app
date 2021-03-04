package com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit;

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
public class ViewPassTimesFragment extends Fragment implements PassTimesAdapter.PasstimeClickListener, PassTimesAdapter.ItemCountUpdateListener {

    private RecyclerView passTimeRecycler;

    @Inject
    WellbeingDatabase db;

    private LiveData<List<ActivityRecord>> passTimes;
    private Observer<List<ActivityRecord>> passTimeObserver;
    private PassTimesAdapter passtimeAdapter;
    private View root;
    private boolean isEditable;
    private boolean isEdited;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        this.root = inflater.inflate(R.layout.fragment_view_pass_times, container, false);

        WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
            List<ActivityRecord> oldActivities = db.activityRecordDao().getActivitiesInTimeRange(0, 1613509560000L);

            // Only show the button if there are old items
            boolean shouldShowSnackbar = false;
            for(ActivityRecord activity : oldActivities) {
                if(activity.getActivityWayToWellbeing().equals(WaysToWellbeing.UNASSIGNED.toString())) {
                    shouldShowSnackbar = true;
                    break;
                }
            }

            if(shouldShowSnackbar) {
                requireActivity().runOnUiThread(() -> {
                    Snackbar snackbar = Snackbar.make(root, getText(R.string.old_activity_warning), Snackbar.LENGTH_LONG)
                        .setAnchorView(root.findViewById(R.id.create_activity_button))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setAction(R.string.button_edit, v -> {
                            makeEditable();
                        });
                    snackbar.show();
                });
            }
        });

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

        // Reference https://stackoverflow.com/a/47531110/13496270
        setHasOptionsMenu(true);

        return this.root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View view2 = requireActivity().findViewById(R.id.snackbar_layout);

    }

    public void onCreateFromSearchButtonClicked(View v) {
        EditText searchText = this.root.findViewById(R.id.passtime_search_box);
        String searchQuery = searchText.getText().toString();
        Intent createActivityIntent = new Intent(getContext(), CreateOrUpdatePassTimeActivity.class);

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
            passtimeBundle.putBoolean("is_edited", this.isEdited);
            passtimeResult.putExtras(passtimeBundle);
            getActivity().setResult(Activity.RESULT_OK, passtimeResult);
            getActivity().finish();
        }
    }

    @Override
    public void onEditClick(View view, ActivityRecord passtime) {

        this.isEdited = true;
        // Launch activity to update a passtime
        Intent editActivityIntent = new Intent(getActivity(), CreateOrUpdatePassTimeActivity.class);
        Bundle editBundle = new Bundle();
        editBundle.putLong("activity_id", passtime.getActivityRecordId());
        editBundle.putString("activity_name", passtime.getActivityName());
        editBundle.putString("activity_type", passtime.getActivityType());
        editBundle.putString("activity_way_to_wellbeing", passtime.getActivityWayToWellbeing());
        editActivityIntent.putExtras(editBundle);
        startActivity(editActivityIntent);
    }

    @Override
    public void onDeleteClick(View view, ActivityRecord passtime) {
        new MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.title_delete_activity)
            .setMessage(R.string.body_delete_activity)
            .setIcon(R.drawable.icon_close)
            .setPositiveButton(R.string.button_delete, (dialog, which) -> {
                // Set the pass time to hidden
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    this.db.activityRecordDao().flagHidden(passtime.getActivityRecordId(), true);
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
        this.passtimeAdapter.editableList(this.isEditable);
    }
}
