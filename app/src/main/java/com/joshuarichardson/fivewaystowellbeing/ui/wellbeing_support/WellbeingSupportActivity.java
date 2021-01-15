package com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support;

import android.os.Bundle;

import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WellbeingSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellbeing_support);

        RecyclerView recycler = findViewById(R.id.wellbeing_support_recycler_view);

        recycler.setLayoutManager(new LinearLayoutManager(WellbeingSupportActivity.this));

        ArrayList<WellbeingSupportItem> supportList = new ArrayList<>();
        supportList.add(new WellbeingSupportItem("11", "12", 13, "14"));
        supportList.add(new WellbeingSupportItem("21", "22", 23, "24"));
        supportList.add(new WellbeingSupportItem("21", "22", 23, "24"));



        WellbeingSupportAdapter supportAdapter = new WellbeingSupportAdapter(WellbeingSupportActivity.this, supportList);
        recycler.setAdapter(supportAdapter);
    }
}