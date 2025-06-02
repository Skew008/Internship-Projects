package com.panicatthedebug.pathsync.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "custom_learning_paths")
public class CustomLearningPath {
    @Id
    private String id;

    private Map<String, Topic> topics = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Topic> getTopics() {
        return topics;
    }

    public void setTopics(Map<String, Topic> topics) {
        this.topics = topics;
    }

    @JsonAnySetter
    public void addTopic(String key, Topic value) {
        this.topics.put(key, value);
    }

    public static class Topic {
        private Map<String, SubTopic> subTopics = new HashMap<>();

        public Map<String, SubTopic> getSubTopics() {
            return subTopics;
        }

        public void setSubTopics(Map<String, SubTopic> subTopics) {
            this.subTopics = subTopics;
        }

        @JsonAnySetter
        public void addSubTopic(String key, SubTopic value) {
            this.subTopics.put(key, value);
        }

        @Override
        public String toString() {
            return "Topic{" +
                    "subTopics=" + subTopics +
                    '}';
        }
    }

    public static class SubTopic {
        private String duration;
        private String completionStatus;
        private Map<String, String> learningResources = new HashMap<>();

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getCompletionStatus() {
            return completionStatus;
        }

        public void setCompletionStatus(String completionStatus) {
            this.completionStatus = completionStatus;
        }

        public Map<String, String> getLearningResources() {
            return learningResources;
        }

        public void setLearningResources(Map<String, String> learningResources) {
            this.learningResources = learningResources;
        }

        @Override
        public String toString() {
            return "SubTopic{" +
                    "duration='" + duration + '\'' +
                    ", completionStatus='" + completionStatus + '\'' +
                    ", learningResources=" + learningResources +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CustomLearningPath{" +
                "id='" + id + '\'' +
                ", topics=" + topics +
                '}';
    }
}