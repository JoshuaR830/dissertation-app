package com.joshuarichardson.fivewaystowellbeing.ui.history;

import com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit.ViewPassTimesFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Reference: https://developer.android.com/guide/navigation/navigation-swipe-view-2
public class SurveyResponsesPagerAdapter extends FragmentStateAdapter {

    public SurveyResponsesPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    // Reference: https://stackoverflow.com/a/58527435/13496270
    public Fragment createFragment(int position) {

        Fragment fragment;
        if(position == 0) {
            fragment = new HistoryFragment();
            return fragment;
        } else if(position == 1) {
            fragment = new ViewPassTimesFragment();
            return fragment;
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}