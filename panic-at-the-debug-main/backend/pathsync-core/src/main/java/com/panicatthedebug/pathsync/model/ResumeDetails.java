package com.panicatthedebug.pathsync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "resumeDetails")
public class ResumeDetails {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> skills;

    public ResumeDetails() {
    }

    public ResumeDetails(String firstName, String lastName, String email, List<String> skills) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.skills = skills;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
