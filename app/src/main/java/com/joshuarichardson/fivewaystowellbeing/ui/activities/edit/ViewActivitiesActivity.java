package com.joshuarichardson.fivewaystowellbeing.ui.activities.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.joshuarichardson.fivewaystowellbeing.MenuItemHelper;
import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * An activity that displays a list of activities by displaying the fragment
 */
@AndroidEntryPoint
public class ViewActivitiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_activities);
    }

    public void onCreateActivityButtonClicked(View v) {
        Intent answerSurveyIntent = new Intent(this, CreateOrUpdateActivityActivity.class);
        startActivity(answerSurveyIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            if(getSupportFragmentManager().findFragmentById(R.id.activities_fragment).getClass() == ActivityHistoryFragment.class) {
                ActivityHistoryFragment viewSurveyResponseFragment = (ActivityHistoryFragment) getSupportFragmentManager().findFragmentById(R.id.activities_fragment);
                if(viewSurveyResponseFragment == null) {
                    return false;
                }
                viewSurveyResponseFragment.makeActivitiesEditable();
            }
            return true;
        }

        Intent intent = MenuItemHelper.handleOverflowMenuClick(this, item);

        if(intent == null) {
            return false;
        }

        // This only runs if an intent was set
        startActivity(intent);
        return true;
    }
}
