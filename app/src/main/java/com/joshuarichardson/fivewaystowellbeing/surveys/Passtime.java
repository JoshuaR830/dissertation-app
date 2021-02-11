package com.joshuarichardson.fivewaystowellbeing.surveys;

import java.util.ArrayList;
import java.util.List;

public class Passtime {

    private final String name;
    private final String note;
    private final String type;

    ArrayList<Question> questions;

    public Passtime(String name, String note, String type) {
        this.questions = new ArrayList<>();
        this.name = name;
        this.note = note;
        this.type = type;
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
}
