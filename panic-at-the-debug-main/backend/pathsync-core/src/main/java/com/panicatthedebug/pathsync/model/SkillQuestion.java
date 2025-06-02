package com.panicatthedebug.pathsync.model;

import java.util.List;

public class SkillQuestion {
    private String id;
    private String text;
    private String skillCategory;
    private List<String> options;
    private boolean fromResume;

    public SkillQuestion() {}

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

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public boolean isFromResume() {
        return fromResume;
    }

    public void setFromResume(boolean fromResume) {
        this.fromResume = fromResume;
    }
}