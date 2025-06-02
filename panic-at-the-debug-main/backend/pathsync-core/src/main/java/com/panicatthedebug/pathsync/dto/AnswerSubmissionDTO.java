package com.panicatthedebug.pathsync.dto;

public class AnswerSubmissionDTO {
    private String questionId;
    private Integer selectedOption;

    // Getters, setters
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Integer getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(Integer selectedOption) {
        this.selectedOption = selectedOption;
    }
}
