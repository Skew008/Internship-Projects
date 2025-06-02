package com.panicatthedebug.pathsync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document(collection = "survey_responses")
public class SurveyResponse {
    @Id
    private String id;
    private String userEmail;
    private String targetRole;      // "Backend Engineer", "Frontend Engineer", "Testing Engineer"
    private String preferredLanguage; // "Java", "JavaScript"
    private Map<String, String> skillAssessments; // skill -> BEGINNER/INTERMEDIATE/ADVANCED
    private String overallSkillLevel; // BEGINNER/INTERMEDIATE/ADVANCED
    private Date completedAt;

    public SurveyResponse(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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