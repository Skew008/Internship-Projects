package com.panicatthedebug.pathsync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "survey_definitions")
public class SurveyDefinition {
    @Id
    private String id;
    private String targetRole;
    private String language;
    private List<SkillQuestion> questions;
    private boolean active;

    public SurveyDefinition() {}

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<SkillQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SkillQuestion> questions) {
        this.questions = questions;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}