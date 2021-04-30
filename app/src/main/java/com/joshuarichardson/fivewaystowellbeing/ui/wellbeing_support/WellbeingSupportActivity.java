package com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support;

import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Activity displaying wellbeing support to users.
 * Links are provided which open in a webview
 */
public class WellbeingSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellbeing_support);

        // Add the wellbeing support items to the recycler view
        RecyclerView supportRecyclerView = findViewById(R.id.wellbeing_support_recycler_view);
        supportRecyclerView.setLayoutManager(new LinearLayoutManager(WellbeingSupportActivity.this));
        WellbeingSupportAdapter supportAdapter = new WellbeingSupportAdapter(WellbeingSupportActivity.this, SupportListHelper.getList(getApplicationContext()));
        supportRecyclerView.setAdapter(supportAdapter);
    }
}