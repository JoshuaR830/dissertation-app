package com.joshuarichardson.fivewaystowellbeing;

import java.util.ArrayList;
import java.util.List;

public class DropDownHelper {
    public static List<String> getEnumStrings(ActivityType[] values) {
        ArrayList<String> enumNames = new ArrayList<>();
        for(ActivityType value : values) {
            String val = value.toString();
            enumNames.add(val.substring(0, 1).toUpperCase() + val.substring(1).toLowerCase());
        }

        return enumNames;
    }
}
