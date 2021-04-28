package com.joshuarichardson.fivewaystowellbeing.ui.history;

import com.joshuarichardson.fivewaystowellbeing.ui.activities.edit.ActivityHistoryFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Reference: https://developer.android.com/guide/navigation/navigation-swipe-view-2
public class HistoryPagerAdapter extends FragmentStateAdapter {

    public HistoryPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    // Reference: https://stackoverflow.com/a/58527435/13496270
    public Fragment createFragment(int position) {

        Fragment fragment;
        if(position == 0) {
            fragment = new SurveyHistoryFragment();
            return fragment;
        } else if(position == 1) {
            fragment = new ActivityHistoryFragment();
            return fragment;
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}