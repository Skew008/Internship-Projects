package com.panicatthedebug.pathsync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user_progress")
public class UserProgress {

    @Id
    private String id;
    private List<ProgressEntry> progressEntries;
    private int completionPercentage;

    public UserProgress(){}

    public UserProgress(String id, List<ProgressEntry> progressEntries, int completionPercentage) {
        this.id = id;
        this.progressEntries = progressEntries;
        this.completionPercentage = completionPercentage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ProgressEntry> getProgressEntries() {
        return progressEntries;
    }

    public void setProgressEntries(List<ProgressEntry> progressEntries) {
        this.progressEntries = progressEntries;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public static class ProgressEntry {
        private String topicName;
        private String subTopicName;
        private String estimatedCompletionDate; // Calculated based on duration
        private boolean completionStatus; // Not Started, In Progress, Completed
        private String duration; // Duration in hours

        public ProgressEntry(String topicName, String subTopicName, String estimatedCompletionDate, boolean completionStatus, String duration) {
            this.topicName = topicName;
            this.subTopicName = subTopicName;
            this.estimatedCompletionDate = estimatedCompletionDate;
            this.completionStatus = completionStatus;
            this.duration = duration;
        }

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getSubTopicName() {
            return subTopicName;
        }

        public void setSubTopicName(String subTopicName) {
            this.subTopicName = subTopicName;
        }

        public String getEstimatedCompletionDate() {
            return estimatedCompletionDate;
        }

        public void setEstimatedCompletionDate(String estimatedCompletionDate) {
            this.estimatedCompletionDate = estimatedCompletionDate;
        }

        public boolean getCompletionStatus() {
            return completionStatus;
        }

        public void setCompletionStatus(boolean completionStatus) {
            this.completionStatus = completionStatus;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }
}
