package com.panicatthedebug.pathsync.dto;

import com.panicatthedebug.pathsync.model.Question;

import java.util.List;

public class QuestionDTO {
    private String id;
    private String text;
    private List<String> options;
    private Integer userSelectedOption;
    private String skillCategory;
    private String difficultyLevel; // Added field

    // Additional fields only shown when assessment is completed
    private Integer correctOptionIndex;
    private String explanation;

    // Static factory method
    public static QuestionDTO from(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setOptions(question.getOptions());
        dto.setUserSelectedOption(question.getUserSelectedOption());
        dto.setSkillCategory(question.getSkillCategory());
        dto.setDifficultyLevel(question.getDifficultyLevel());

        // Only include answers if assessment is completed
        // This will be set in the controller based on assessment status

        return dto;
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

    public Integer getUserSelectedOption() {
        return userSelectedOption;
    }

    public void setUserSelectedOption(Integer userSelectedOption) {
        this.userSelectedOption = userSelectedOption;
    }

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    public Integer getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(Integer correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}