package com.panicatthedebug.pathsync.dto;

import java.util.Map;

public class SurveySubmissionDTO {
    private String targetRole;      // "Backend Engineer", "Frontend Engineer", "Testing Engineer"
    private String preferredLanguage; // "Java", "JavaScript"
    private Map<String, Integer> skillResponses; // questionId -> responseIndex (0-based)

    public SurveySubmissionDTO(String targetRole, String preferredLanguage, Map<String, Integer> skillResponses) {
        this.targetRole = targetRole;
        this.preferredLanguage = preferredLanguage;
        this.skillResponses = skillResponses;
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

    public Map<String, Integer> getSkillResponses() {
        return skillResponses;
    }

    public void setSkillResponses(Map<String, Integer> skillResponses) {
        this.skillResponses = skillResponses;
    }
}