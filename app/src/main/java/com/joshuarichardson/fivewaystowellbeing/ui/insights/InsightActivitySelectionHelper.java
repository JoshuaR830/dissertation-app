package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import com.joshuarichardson.fivewaystowellbeing.storage.ActivityStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InsightActivitySelectionHelper {

    public static long selectMostAchieved(List<ActivityStats> activityStats) {
        if(activityStats.size() == 0) {
            return 0;
        }

        // Already know what the max is because the list is sorted
        int max = activityStats.get(0).getCount();

        return getRandomItem(max, activityStats);
    }

    public static long selectLeastAchieved(List<ActivityStats> activityStats, boolean isComparedToMax) {
        int numItems = activityStats.size();

        if (numItems == 0) {
            return 0;
        }

        if(isComparedToMax) {
            if (numItems == 1) {
                return 0;
            }

            // Compare the max and min values - if they are the same don't want to display it as worst
            if(activityStats.get(0).getCount() == activityStats.get(numItems - 1).getCount()) {
                return 0;
            }
        }

        int min = activityStats.get(numItems - 1).getCount();

        return getRandomItem(min, activityStats);
    }

    private static long getRandomItem(int condition, List<ActivityStats> activityStats) {

        List<Long> itemList = new ArrayList<>();

        for(ActivityStats activityStat : activityStats) {
            if(activityStat.getCount() == condition) {
                itemList.add(activityStat.getActivityId());
            }
        }

        Collections.shuffle(itemList);

        return itemList.get(0);
    }
}
