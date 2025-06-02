package com.panicatthedebug.pathsync.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Document(collection = "standard_learning_paths")
public class StandardLearningPath {
    @JsonProperty("_id")
    @Id
    private String id;
    @NotNull(message = "Target role is required")
    private String targetRole;
    @NotNull(message = "Target language is required")
    private String targetLanguage;
    @NotNull(message = "Topics list cannot be empty")
    private List<String> topics;

    public StandardLearningPath(String id, String targetRole, String targetLanguage, List<String> topics) {
        this.id = id;
        this.targetRole = targetRole;
        this.targetLanguage = targetLanguage;
        this.topics = topics;
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

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StandardLearningPath that = (StandardLearningPath) o;
        return Objects.equals(id, that.id) && Objects.equals(targetRole, that.targetRole) && Objects.equals(targetLanguage, that.targetLanguage) && Objects.equals(topics, that.topics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, targetRole, targetLanguage, topics);
    }

    @Override
    public String toString() {
        return "StandardLearningPath{" +
                "id='" + id + '\'' +
                ", targetRole='" + targetRole + '\'' +
                ", targetLanguage='" + targetLanguage + '\'' +
                ", topics=" + topics +
                '}';
    }
}
