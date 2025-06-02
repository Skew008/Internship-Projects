package com.panicatthedebug.pathsync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "active_sessions")
public class ActiveSession {

    @Id
    private String id;
    private String userEmail;
    private LocalDateTime startTime;
    private LocalDateTime lastActivityTime;
    private boolean isActive;
    private long totalActiveTimeSeconds;
    private boolean breakNotificationSent;

    public ActiveSession() {
        // Default constructor
    }

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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getTotalActiveTimeSeconds() {
        return totalActiveTimeSeconds;
    }

    public void setTotalActiveTimeSeconds(long totalActiveTimeSeconds) {
        this.totalActiveTimeSeconds = totalActiveTimeSeconds;
    }

    public boolean isBreakNotificationSent() {
        return breakNotificationSent;
    }

    public void setBreakNotificationSent(boolean breakNotificationSent) {
        this.breakNotificationSent = breakNotificationSent;
    }
}
