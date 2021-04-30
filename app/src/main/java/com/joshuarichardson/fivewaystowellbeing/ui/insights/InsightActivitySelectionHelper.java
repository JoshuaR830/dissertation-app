package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import com.joshuarichardson.fivewaystowellbeing.storage.ActivityStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A helper to ensure that random insights are selected that match the condition
 */
public class InsightActivitySelectionHelper {

    /**
     * Get one of the most achieved activities
     *
     * @param activityStats A list of activity ids with associated occurrences
     * @return The random id of an activity that is most achieved
     */
    public static long selectMostAchieved(List<ActivityStats> activityStats) {
        if(activityStats.size() == 0) {
            return 0;
        }

        // Already know what the max is because the list is sorted
        int max = activityStats.get(0).getCount();

        return getRandomItem(max, activityStats);
    }

    /**
     * Get one of the least achieved activities.
     * If compared to max, then if there is only 1 item, it won't be displayed and if multiple
     * items are equal then it won't be displayed to avoid displaying a suggestion that is the same as max.
     *
     * @param activityStats A list of activity ids with associated occurrences
     * @param isComparedToMax Can optionally compare to the maximum value
     * @return The random id of an activity that is least achieved
     */
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

    /**
     * Pick a random item that matches the condition
     *
     * @param condition A condition which must be matched exactly
     * @param activityStats All of the activities with their associated occurrence
     * @return A single item matching the condition
     */
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
