package com.joshuarichardson.fivewaystowellbeing.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.ui.activities.edit.ActivityHistoryFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment allowing for tabs to wellbeing logs and activities
 */
@AndroidEntryPoint
public class HistoryParentFragment extends Fragment {
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

        HistoryPagerAdapter adapter = new HistoryPagerAdapter(this);

        ViewPager2 viewPager = view.findViewById(R.id.history_pager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.history_tabs);
        new TabLayoutMediator(tabLayout, viewPager, ((tab, position) -> {
            if(position == 0) {
                tab.setText(R.string.wellbeing_logs_title);
            } else if (position == 1){
                tab.setText(R.string.navigation_activities);
            }
        })).attach();
    }

    /**
     * Enable editing on the activities
     */
    public void instantiateEditable() {
        TabLayout tabLayout = requireActivity().findViewById(R.id.history_tabs);

        if (tabLayout.getSelectedTabPosition() == 1) {
            // Reference https://stackoverflow.com/a/61178226/13496270
            Fragment activeFragment = getChildFragmentManager().findFragmentByTag("f1");
            if (activeFragment == null) {
                return;
            }

            if (activeFragment.getClass() != ActivityHistoryFragment.class) {
                return;
            }

            ActivityHistoryFragment activityFragment = (ActivityHistoryFragment) activeFragment;
            activityFragment.makeActivitiesEditable();
        }
    }
}
