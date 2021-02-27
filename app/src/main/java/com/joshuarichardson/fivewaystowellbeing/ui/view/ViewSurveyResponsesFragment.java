package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewSurveyResponsesFragment extends Fragment {
    @Inject
    WellbeingDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_surveys, parentView, false);

        return root;
    }

    @Override
    // Reference https://developer.android.com/guide/navigation/navigation-swipe-view-2
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SurveyResponsesPagerAdapter adapter = new SurveyResponsesPagerAdapter(this);

        ViewPager2 viewPager = view.findViewById(R.id.history_pager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.history_tabs);
        new TabLayoutMediator(tabLayout, viewPager, ((tab, position) -> {
            if(position == 0) {
                tab.setText(R.string.wellbeing_logs_title);
            } else {
                tab.setText(R.string.navigation_pass_times);
            }
        })).attach();
    }
}
