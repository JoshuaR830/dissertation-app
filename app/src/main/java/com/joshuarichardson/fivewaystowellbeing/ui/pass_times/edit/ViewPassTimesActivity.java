package com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit;

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

@AndroidEntryPoint
public class ViewPassTimesActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pass_times);
    }

    public void onCreatePassTimeButtonClicked(View v) {
        Intent answerSurveyIntent = new Intent(this, CreateOrUpdatePassTimeActivity.class);
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
            if(getSupportFragmentManager().findFragmentById(R.id.activities_fragment).getClass() == ViewPassTimesFragment.class) {
                ViewPassTimesFragment viewSurveyResponseFragment = (ViewPassTimesFragment) getSupportFragmentManager().findFragmentById(R.id.activities_fragment);
                if(viewSurveyResponseFragment == null) {
                    return false;
                }
                viewSurveyResponseFragment.makeEditable();
            }
            return true;
        }

        Intent intent = MenuItemHelper.handleMenuClick(this, item);

        if(intent == null) {
            return false;
        }

        // This only runs if an intent was set
        startActivity(intent);
        return true;
    }
}
