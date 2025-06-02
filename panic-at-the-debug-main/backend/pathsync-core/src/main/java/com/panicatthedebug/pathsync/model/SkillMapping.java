package com.panicatthedebug.pathsync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "skillMappings")
public class SkillMapping {

    @Id
    private String id;
    private String resumeText;
    private String standardizedSkill;

    public SkillMapping() {
    }

    public SkillMapping(String id, String resumeText, String standardizedSkill) {
        this.id = id;
        this.resumeText = resumeText;
        this.standardizedSkill = standardizedSkill;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getStandardizedSkill() {
        return standardizedSkill;
    }

    public void setStandardizedSkill(String standardizedSkill) {
        this.standardizedSkill = standardizedSkill;
    }
}
