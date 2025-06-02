package com.panicatthedebug.pathsync.dto;

import com.panicatthedebug.pathsync.model.Assessment;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssessmentDTO {
    private String id;
    private String targetRole;
    private String preferredLanguage;
    private String skillLevel;
    private List<QuestionDTO> questions;
    private Date createdAt;
    private Date startedAt;
    private Date completedAt;
    private int durationMinutes;
    private boolean completed;
    private Integer score;
    private long timeRemainingSeconds;
    private Map<String,String> questionResponses=null;

    // Static factory method
    public static AssessmentDTO from(Assessment assessment, long timeRemainingSeconds) {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setId(assessment.getId());
        dto.setTargetRole(assessment.getTargetRole());
        dto.setPreferredLanguage(assessment.getPreferredLanguage());
        dto.setSkillLevel(assessment.getSkillLevel());

        // Map questions to DTOs
        List<QuestionDTO> questionDTOs = assessment.getQuestions().stream()
                .map(QuestionDTO::from)
                .collect(Collectors.toList());
        dto.setQuestions(questionDTOs);

        dto.setCreatedAt(assessment.getCreatedAt());
        dto.setStartedAt(assessment.getStartedAt());
        dto.setCompletedAt(assessment.getCompletedAt());
        dto.setDurationMinutes(assessment.getDurationMinutes());
        dto.setCompleted(assessment.isCompleted());
        dto.setScore(assessment.getScore());
        dto.setTimeRemainingSeconds(timeRemainingSeconds);

        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public long getTimeRemainingSeconds() {
        return timeRemainingSeconds;
    }

    public void setTimeRemainingSeconds(long timeRemainingSeconds) {
        this.timeRemainingSeconds = timeRemainingSeconds;
    }

    public Map<String, String> getQuestionResponses() {
        return questionResponses;
    }

    public void setQuestionResponses(Map<String, String> questionResponses) {
        this.questionResponses = questionResponses;
    }
}