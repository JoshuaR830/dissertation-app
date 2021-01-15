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
        supportList.add(new WellbeingSupportItem("Mind", "Tips and guides for managing with mental health problems.", R.drawable.help_icon_mind, "https://www.mind.org.uk/information-support/tips-for-everyday-living/"));
        supportList.add(new WellbeingSupportItem("NHS", "Information about NHS mental health services.", R.drawable.help_icon_nhs, "https://www.nhs.uk/using-the-nhs/nhs-services/mental-health-services/"));
        supportList.add(new WellbeingSupportItem("Self-help resources", "A selection of self-help resources.", R.drawable.help_icon_self_help, "https://www.annafreud.org/on-my-mind/self-care/"));

        WellbeingSupportAdapter supportAdapter = new WellbeingSupportAdapter(WellbeingSupportActivity.this, supportList);
        recycler.setAdapter(supportAdapter);
    }
}