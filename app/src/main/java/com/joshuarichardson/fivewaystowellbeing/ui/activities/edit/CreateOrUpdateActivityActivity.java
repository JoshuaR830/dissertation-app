package com.joshuarichardson.fivewaystowellbeing.ui.activities.edit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.joshuarichardson.fivewaystowellbeing.ActivityType;
import com.joshuarichardson.fivewaystowellbeing.DropDownHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.analytics.LogAnalyticEventHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.ActivityRecordDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.Arrays;
import java.util.Calendar;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * An Android Activity for creating new activities or editing existing ones.
 */
@AndroidEntryPoint
public class CreateOrUpdateActivityActivity extends AppCompatActivity {
    ActivityRecordDao activityDao;
    private long activityId;
    private boolean isEditing;

    @Inject
    WellbeingDatabase db;

    @Inject
    LogAnalyticEventHelper analyticsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_activity);

        AutoCompleteTextView dropDownInput = findViewById(R.id.activity_type_input);

        AutoCompleteTextView wayToWellbeingInput = findViewById(R.id.way_to_wellbeing_input);
        ArrayAdapter<String> waysToWellbeingAdapter = new ArrayAdapter<>(CreateOrUpdateActivityActivity.this, R.layout.item_list_text, Arrays.asList("Connect", "Be active", "Keep learning", "Take notice", "Give", "None"));
        wayToWellbeingInput.setAdapter(waysToWellbeingAdapter);

        updateWayToWellbeingBasedOnType(dropDownInput);
        displayWellbeingHelpCard(wayToWellbeingInput);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateOrUpdateActivityActivity.this, R.layout.item_list_text, DropDownHelper.getEnumStrings(ActivityType.values()));
        dropDownInput.setAdapter(adapter);

        this.activityDao = this.db.activityRecordDao();

        if (getIntent() != null && getIntent().getExtras() != null) {
            // Set the name if the user entered one
            if(getIntent().hasExtra("new_activity_name")) {
                EditText textBox = findViewById(R.id.activity_name_input);
                textBox.setText(getIntent().getExtras().getString("new_activity_name", ""));
            }

            // If there is an activity id already - it means the user is editing it
            if (getIntent().hasExtra("activity_id")) {
                this.isEditing = true;
                EditText textBox = findViewById(R.id.activity_name_input);
                this.activityId = getIntent().getExtras().getLong("activity_id");
                textBox.setText(getIntent().getExtras().getString("activity_name", ""));
                dropDownInput.setText(getIntent().getExtras().getString("activity_type", ""), false);
                String wayToWellbeing = getIntent().getExtras().getString("activity_way_to_wellbeing", WellbeingHelper.getStringFromWayToWellbeing(WaysToWellbeing.UNASSIGNED));
                wayToWellbeingInput.setText(WellbeingHelper.getStringFromWayToWellbeing(WaysToWellbeing.valueOf(wayToWellbeing)), false);
            }
        }
    }

    /**
     * When an item is selected from the activity type input,
     * update the way to wellbeing type so the user doesn't need to know it
     *
     * @param dropDownInput
     */
    private void updateWayToWellbeingBasedOnType(AutoCompleteTextView dropDownInput) {
        dropDownInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String type = dropDownInput.getText().toString();
                WaysToWellbeing wayToWellbeing = WellbeingHelper.getDefaultWayToWellbeingFromActivityType(type);

                AutoCompleteTextView wayToWellbeingInput = findViewById(R.id.way_to_wellbeing_input);
                wayToWellbeingInput.setText(WellbeingHelper.getStringFromWayToWellbeing(wayToWellbeing), false);
            }
        });
    }

    /**
     * Attach listener to the drop down menu of ways to wellbeing.
     * When a way to wellbeing is selected, display the definiton of the way to wellbeing.
     *
     * @param wayToWellbeingInput The input box to listen to for changes
     */
    private void displayWellbeingHelpCard(AutoCompleteTextView wayToWellbeingInput) {
        LinearLayout helpContainer = findViewById(R.id.wellbeing_card_help_container);

        wayToWellbeingInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String wayToWellbeingSelected = wayToWellbeingInput.getText().toString();
                helpContainer.removeAllViews();
                switch (wayToWellbeingSelected) {
                    case "Connect":
                        LayoutInflater.from(CreateOrUpdateActivityActivity.this).inflate(R.layout.card_connect, helpContainer);
                        helpContainer.setVisibility(View.VISIBLE);
                        break;
                    case "Be active":
                        LayoutInflater.from(CreateOrUpdateActivityActivity.this).inflate(R.layout.card_be_active, helpContainer);
                        helpContainer.setVisibility(View.VISIBLE);
                        break;
                    case "Keep learning":
                        LayoutInflater.from(CreateOrUpdateActivityActivity.this).inflate(R.layout.card_keep_learning, helpContainer);
                        helpContainer.setVisibility(View.VISIBLE);
                        break;
                    case "Take notice":
                        LayoutInflater.from(CreateOrUpdateActivityActivity.this).inflate(R.layout.card_take_notice, helpContainer);
                        helpContainer.setVisibility(View.VISIBLE);
                        break;
                    case "Give":
                        LayoutInflater.from(CreateOrUpdateActivityActivity.this).inflate(R.layout.card_give, helpContainer);
                        helpContainer.setVisibility(View.VISIBLE);
                        break;
                    case "None":
                        helpContainer.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    /**
     * When the user presses the submit button, this gets the form information.
     * If the user made any mistakes, error messages will be displayed and the form won't be submitted
     *
     * If there are no errors and the activity is new, it will be updated in the table,
     * if it is updating an existing activity, then that activity will be updated
     *
     * @param view The instance of the button clicked.
     */
    public void onSubmit(View view) {
        // Log the interaction
        this.analyticsHelper.logCreateActivityEvent(this);

        EditText activityName = findViewById(R.id.activity_name_input);
        EditText activityDuration = findViewById(R.id.activity_duration_input);
        EditText activityType = findViewById(R.id.activity_type_input);
        EditText wayToWellbeingInput = findViewById(R.id.way_to_wellbeing_input);

        String name = activityName.getText().toString();

        int duration = Integer.parseInt(activityDuration.getText().toString());
        String type = activityType.getText().toString();

        long unixTime = Calendar.getInstance().getTimeInMillis();

        String wayToWellbeing = wayToWellbeingInput.getText().toString();

        // Check that the inputs are not empty
        TextInputLayout nameContainer = findViewById(R.id.activity_name_input_container);
        TextInputLayout typeContainer = findViewById(R.id.activity_type_input_container);
        TextInputLayout wellbeingContainer = findViewById(R.id.way_to_wellbeing_input_container);

        boolean hasError = false;

        if(name.length() == 0) {
            nameContainer.setError(getString(R.string.error_no_name_entered));
            hasError = true;
        } else {
            nameContainer.setError(null);
        }

        if(type.length() == 0) {
            typeContainer.setError(getString(R.string.error_no_type_selected));
            hasError = true;
        } else {
            typeContainer.setError(null);
        }

        if(wayToWellbeing.length() == 0) {
            wellbeingContainer.setError(getString(R.string.error_no_way_to_wellbeing_selected));
            hasError = true;
        } else {
            wellbeingContainer.setError(null);
        }

        // Don't submit if there are errors
        if(hasError) {
            return;
        }

        // Add or update activity
        final String wayToWellbeingString = WellbeingHelper.getWayToWellbeingFromString(wayToWellbeing).toString();
        WellbeingDatabaseModule.databaseExecutor.execute(() -> {
            if (this.activityId != 0 && this.isEditing) {
                this.activityDao.update(this.activityId, name, type, wayToWellbeingString, unixTime);
            } else {
                this.activityDao.insert(new ActivityRecord(name, duration, unixTime, type, wayToWellbeingString, false));
            }
            finish();
        });
    }
}