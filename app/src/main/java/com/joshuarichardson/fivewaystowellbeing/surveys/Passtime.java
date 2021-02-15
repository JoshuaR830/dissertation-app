package com.joshuarichardson.fivewaystowellbeing.surveys;

import java.util.ArrayList;
import java.util.List;

public class Passtime {

    private final String name;
    private final String note;
    private final String type;
    private final String wayToWellbeing;

    ArrayList<Question> questions;

    public Passtime(String name, String note, String type, String wayToWellbeing) {
        this.questions = new ArrayList<>();
        this.name = name;
        this.note = note;
        this.type = type;
        this.wayToWellbeing = wayToWellbeing;
    }

    public void addQuestionToList(Question question) {
        this.questions.add(question);
    }

    public List<Question> getQuestions() {
        return this.questions;
    }

    public String getNote() {
        return this.note;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getWayToWellbeing() {
        return this.wayToWellbeing;
    }
}
