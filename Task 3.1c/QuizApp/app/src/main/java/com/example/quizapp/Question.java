package com.example.quizapp;

import java.util.List;

public class Question {
    private String title;
    private String text;
    private List<String> options;
    private int correctAnswerIndex;

    public Question(String title, String text, List<String> options, int correctAnswerIndex) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getTitle() { return title; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
}
