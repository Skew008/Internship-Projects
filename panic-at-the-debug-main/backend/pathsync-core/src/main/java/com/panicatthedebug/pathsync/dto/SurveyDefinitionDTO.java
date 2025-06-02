package com.panicatthedebug.pathsync.dto;

import com.panicatthedebug.pathsync.model.*;

import java.util.List;

public class SurveyDefinitionDTO {
    private String id;
    private String targetRole;
    private String language;
    private List<SkillQuestion> questions;

    // Static factory method
    public static SurveyDefinitionDTO from(SurveyDefinition definition) {
        SurveyDefinitionDTO dto = new SurveyDefinitionDTO();
        dto.setId(definition.getId());
        dto.setTargetRole(definition.getTargetRole());
        dto.setLanguage(definition.getLanguage());
        dto.setQuestions(definition.getQuestions());
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
}