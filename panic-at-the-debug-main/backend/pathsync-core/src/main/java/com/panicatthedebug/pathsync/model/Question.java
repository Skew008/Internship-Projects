package com.panicatthedebug.pathsync.model;

import java.util.List;

public class Question {
    private String id;
    private String text;
    private List<String> options;
    private int correctOptionIndex;
    private String skillCategory;
    private String explanation;
    private Integer userSelectedOption;
    private String difficultyLevel;// Tracks user's answer

    public Question() {}

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getUserSelectedOption() {
        return userSelectedOption;
    }

    public void setUserSelectedOption(Integer userSelectedOption) {
        this.userSelectedOption = userSelectedOption;
    }
}