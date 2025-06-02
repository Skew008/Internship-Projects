package com.panicatthedebug.pathsync.dto;

import com.panicatthedebug.pathsync.model.SurveyResponse;

import java.util.Date;
import java.util.Map;

public class SurveyResponseDTO {
    private String id;
    private String targetRole;
    private String preferredLanguage;
    private Map<String, String> skillAssessments;
    private String overallSkillLevel;
    private Date completedAt;

    // Static factory method
    public static SurveyResponseDTO from(SurveyResponse response) {
        SurveyResponseDTO dto = new SurveyResponseDTO();
        dto.setId(response.getId());
        dto.setTargetRole(response.getTargetRole());
        dto.setPreferredLanguage(response.getPreferredLanguage());
        dto.setSkillAssessments(response.getSkillAssessments());
        dto.setOverallSkillLevel(response.getOverallSkillLevel());
        dto.setCompletedAt(response.getCompletedAt());
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

    public Map<String, String> getSkillAssessments() {
        return skillAssessments;
    }

    public void setSkillAssessments(Map<String, String> skillAssessments) {
        this.skillAssessments = skillAssessments;
    }

    public String getOverallSkillLevel() {
        return overallSkillLevel;
    }

    public void setOverallSkillLevel(String overallSkillLevel) {
        this.overallSkillLevel = overallSkillLevel;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }
}