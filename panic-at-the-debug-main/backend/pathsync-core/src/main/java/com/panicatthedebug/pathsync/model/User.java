package com.panicatthedebug.pathsync.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "users")
public class User {
    private String firstName;
    private String lastName;
    @Id
    private String email;
    private String password;
    private String role;
    private String targetRole;
    private String targetLanguage;
    private boolean assessmentCompleted;
    private String finalSkillLevel;

    public User(String firstName, String lastName, String email, String password, String role, String targetRole, String targetLanguage, boolean assessmentCompleted) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.targetRole = targetRole;
        this.targetLanguage = targetLanguage;
        this.assessmentCompleted = assessmentCompleted;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public boolean isAssessmentCompleted() {
        return assessmentCompleted;
    }

    public void setAssessmentCompleted(boolean assessmentCompleted) {
        this.assessmentCompleted = assessmentCompleted;
    }

    public String getFinalSkillLevel() {
        return finalSkillLevel;
    }

    public void setFinalSkillLevel(String finalSkillLevel) {
        this.finalSkillLevel = finalSkillLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return assessmentCompleted == user.assessmentCompleted && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(role, user.role) && Objects.equals(targetRole, user.targetRole) && Objects.equals(targetLanguage, user.targetLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, password, role, targetRole, targetLanguage, assessmentCompleted);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", targetRole='" + targetRole + '\'' +
                ", targetLanguage='" + targetLanguage + '\'' +
                ", assessmentCompleted=" + assessmentCompleted +
                '}';
    }
}
