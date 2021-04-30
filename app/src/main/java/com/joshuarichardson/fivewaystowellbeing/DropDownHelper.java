package com.joshuarichardson.fivewaystowellbeing;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper to access the enum strings
 */
public class DropDownHelper {
    /**
     * Convert enum to human readable string with sentence case
     *
     * @param values List of activity types
     * @return A list of strings
     */
    public static List<String> getEnumStrings(ActivityType[] values) {
        ArrayList<String> enumNames = new ArrayList<>();
        for(ActivityType value : values) {
            String val = value.toString();
            enumNames.add(val.substring(0, 1).toUpperCase() + val.substring(1).toLowerCase());
        }

        return enumNames;
    }
}
