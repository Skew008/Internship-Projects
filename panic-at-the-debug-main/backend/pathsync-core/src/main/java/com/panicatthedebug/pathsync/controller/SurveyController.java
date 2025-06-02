package com.panicatthedebug.pathsync.controller;

import com.panicatthedebug.pathsync.exception.ResourceNotFoundException;
import com.panicatthedebug.pathsync.exception.UserNotFoundException;
import com.panicatthedebug.pathsync.model.*;
import com.panicatthedebug.pathsync.dto.*;
import com.panicatthedebug.pathsync.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService surveyService;

    @Autowired
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/definitions")
    public ResponseEntity<List<String>> getAvailableRoles() {
        // Return available roles for the initial selection
        return ResponseEntity.ok(List.of(
                "Backend Engineer",
                "Frontend Engineer",
                "Testing Engineer"
        ));
    }

    @GetMapping("/definitions/{role}/languages")
    public ResponseEntity<List<String>> getAvailableLanguages(@PathVariable String role) {
        // Return available languages for the selected role
        List<String> languages;
        switch (role) {
            case "Backend Engineer" -> languages = List.of("Java");
            case "Frontend Engineer" -> languages = List.of("JavaScript");
            case "Testing Engineer" -> languages = List.of("Java");
            default -> languages = List.of();
        }
        return ResponseEntity.ok(languages);
    }

    @GetMapping("/definitions/{role}/{language}")
    public ResponseEntity<SurveyDefinitionDTO> getSurveyDefinition(
            Authentication authentication,
            @PathVariable String role,
            @PathVariable String language) throws ResourceNotFoundException {
        SurveyDefinition definition = surveyService.getSurveyDefinition(authentication.getName(),role, language);
        return ResponseEntity.ok(SurveyDefinitionDTO.from(definition));
    }

    @PostMapping
    public ResponseEntity<SurveyResponseDTO> submitSurvey(
            @RequestBody SurveySubmissionDTO submission,
            Authentication authentication) throws UserNotFoundException, ResourceNotFoundException {
        String userId = authentication.getName();
        SurveyResponse response = surveyService.submitSurvey(userId, submission);
        return ResponseEntity.ok(SurveyResponseDTO.from(response));
    }

    @GetMapping
    public ResponseEntity<SurveyResponseDTO> getUserSurvey(Authentication authentication) {
        String userId = authentication.getName();
        return surveyService.getUserSurvey(userId)
                .map(SurveyResponseDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
