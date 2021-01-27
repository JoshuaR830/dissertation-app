package com.joshuarichardson.fivewaystowellbeing.surveys;

import java.util.List;

public class DropDownListOptionWrapper {
    private List<String> optionsList;

    public DropDownListOptionWrapper(List<String> options) {
        this.optionsList = options;
    }

    public List<String> getOptionsList() {
        return this.optionsList;
    }
}
