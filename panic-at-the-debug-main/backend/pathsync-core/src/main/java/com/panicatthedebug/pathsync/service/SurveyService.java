package com.panicatthedebug.pathsync.service;

import com.panicatthedebug.pathsync.exception.UserNotFoundException;
import com.panicatthedebug.pathsync.model.*;
import com.panicatthedebug.pathsync.dto.SurveySubmissionDTO;
import com.panicatthedebug.pathsync.exception.ResourceNotFoundException;
import com.panicatthedebug.pathsync.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SurveyService {

    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyDefinitionRepository surveyDefinitionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ResumeRepo resumeRepo;

    public SurveyService(
            SurveyResponseRepository surveyResponseRepository,
            SurveyDefinitionRepository surveyDefinitionRepository,
            UserRepository userRepository,
            UserService userService,
            ResumeRepo resumeRepo) {
        this.surveyResponseRepository = surveyResponseRepository;
        this.surveyDefinitionRepository = surveyDefinitionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.resumeRepo = resumeRepo;
    }

//    @PostConstruct
//    public void initializeSurveyDefinitions() {
//        // Check if survey definitions exist
//        if (surveyDefinitionRepository.count() == 0) {
//            createDefaultSurveyDefinitions();
//        }
//    }

//    private void createDefaultSurveyDefinitions() {
//        // Create Backend Java survey
//        SurveyDefinition backendJava = new SurveyDefinition();
//        backendJava.setId(UUID.randomUUID().toString());
//        backendJava.setTargetRole("Backend Engineer");
//        backendJava.setLanguage("Java");
//        backendJava.setActive(true);
//        backendJava.setQuestions(createBackendJavaQuestions());
//        surveyDefinitionRepository.save(backendJava);
//
//        // Create Frontend JavaScript survey
//        SurveyDefinition frontendJs = new SurveyDefinition();
//        frontendJs.setId(UUID.randomUUID().toString());
//        frontendJs.setTargetRole("Frontend Engineer");
//        frontendJs.setLanguage("JavaScript");
//        frontendJs.setActive(true);
//        frontendJs.setQuestions(createFrontendJsQuestions());
//        surveyDefinitionRepository.save(frontendJs);
//
//        // Create Testing Java survey
//        SurveyDefinition testingJava = new SurveyDefinition();
//        testingJava.setId(UUID.randomUUID().toString());
//        testingJava.setTargetRole("Testing Engineer");
//        testingJava.setLanguage("Java");
//        testingJava.setActive(true);
//        testingJava.setQuestions(createTestingJavaQuestions());
//        surveyDefinitionRepository.save(testingJava);
//    }
//
//    private List<SkillQuestion> createBackendJavaQuestions() {
//        List<SkillQuestion> questions = new ArrayList<>();
//
//        // Question 1
//        SkillQuestion q1 = new SkillQuestion();
//        q1.setId("bj1");
//        q1.setText("How comfortable are you with Java syntax and core concepts?");
//        q1.setSkillCategory("Java");
//        q1.setOptions(List.of(
//                "I'm new to Java or have limited knowledge", // BEGINNER
//                "I understand basic syntax and can write programs with OOP concepts", // INTERMEDIATE
//                "I have deep knowledge of Java including advanced features" // ADVANCED
//        ));
//        questions.add(q1);
//
//        // Question 2
//        SkillQuestion q2 = new SkillQuestion();
//        q2.setId("bj2");
//        q2.setText("Have you worked with Spring Boot before?");
//        q2.setSkillCategory("Spring Boot");
//        q2.setOptions(List.of(
//                "Never used it or only in basic tutorials", // BEGINNER
//                "I've built applications with Spring Boot", // INTERMEDIATE
//                "I've built complex enterprise applications with Spring Boot" // ADVANCED
//        ));
//        questions.add(q2);
//
//        // Question 3
//        SkillQuestion q3 = new SkillQuestion();
//        q3.setId("bj3");
//        q3.setText("How experienced are you with designing and implementing RESTful APIs?");
//        q3.setSkillCategory("REST APIs");
//        q3.setOptions(List.of(
//                "No experience or limited conceptual understanding", // BEGINNER
//                "I've designed and implemented several RESTful APIs", // INTERMEDIATE
//                "I've designed complex API architectures following best practices" // ADVANCED
//        ));
//        questions.add(q3);
//
//        // Question 4
//        SkillQuestion q4 = new SkillQuestion();
//        q4.setId("bj4");
//        q4.setText("How would you rate your SQL knowledge?");
//        q4.setSkillCategory("SQL");
//        q4.setOptions(List.of(
//                "I know basic queries (SELECT, INSERT, UPDATE, DELETE)", // BEGINNER
//                "I'm comfortable with joins, subqueries, and database design", // INTERMEDIATE
//                "I have advanced SQL knowledge including optimization and performance tuning" // ADVANCED
//        ));
//        questions.add(q4);
//
//        // Question 5
//        SkillQuestion q5 = new SkillQuestion();
//        q5.setId("bj5");
//        q5.setText("How familiar are you with microservices architecture?");
//        q5.setSkillCategory("Microservices");
//        q5.setOptions(List.of(
//                "Limited understanding of the concept", // BEGINNER
//                "I've worked on microservices-based applications", // INTERMEDIATE
//                "I've designed and implemented complex microservices architectures" // ADVANCED
//        ));
//        questions.add(q5);
//
//        return questions;
//    }
//
//    private List<SkillQuestion> createFrontendJsQuestions() {
//        List<SkillQuestion> questions = new ArrayList<>();
//
//        // Question 1
//        SkillQuestion q1 = new SkillQuestion();
//        q1.setId("fj1");
//        q1.setText("How comfortable are you with JavaScript syntax and core concepts?");
//        q1.setSkillCategory("JavaScript");
//        q1.setOptions(List.of(
//                "I'm new to JavaScript or have limited knowledge", // BEGINNER
//                "I'm comfortable with most JS features including ES6+", // INTERMEDIATE
//                "I have deep knowledge of JavaScript including advanced patterns" // ADVANCED
//        ));
//        questions.add(q1);
//
//        // Question 2
//        SkillQuestion q2 = new SkillQuestion();
//        q2.setId("fj2");
//        q2.setText("Have you worked with React before?");
//        q2.setSkillCategory("React");
//        q2.setOptions(List.of(
//                "Never used it or only in basic tutorials", // BEGINNER
//                "I've built multiple applications with React", // INTERMEDIATE
//                "I've built complex applications with advanced React patterns" // ADVANCED
//        ));
//        questions.add(q2);
//
//        // Question 3
//        SkillQuestion q3 = new SkillQuestion();
//        q3.setId("fj3");
//        q3.setText("How experienced are you with Node.js?");
//        q3.setSkillCategory("Node.js");
//        q3.setOptions(List.of(
//                "No experience or only simple scripts", // BEGINNER
//                "I've built several applications with Node.js", // INTERMEDIATE
//                "I've built complex backend systems with Node.js" // ADVANCED
//        ));
//        questions.add(q3);
//
//        // Question 4
//        SkillQuestion q4 = new SkillQuestion();
//        q4.setId("fj4");
//        q4.setText("How would you rate your HTML/CSS knowledge?");
//        q4.setSkillCategory("HTML/CSS");
//        q4.setOptions(List.of(
//                "I can create basic web pages with simple styling", // BEGINNER
//                "I'm comfortable with responsive layouts and modern CSS features", // INTERMEDIATE
//                "I have expert-level knowledge of HTML5 and CSS3 including animations and optimization" // ADVANCED
//        ));
//        questions.add(q4);
//
//        // Question 5
//        SkillQuestion q5 = new SkillQuestion();
//        q5.setId("fj5");
//        q5.setText("How familiar are you with state management in React (Redux, Context API, etc.)?");
//        q5.setSkillCategory("State Management");
//        q5.setOptions(List.of(
//                "Limited understanding of state management concepts", // BEGINNER
//                "I've used Redux or Context API in several projects", // INTERMEDIATE
//                "I've implemented complex state management solutions" // ADVANCED
//        ));
//        questions.add(q5);
//
//        return questions;
//    }
//
//    private List<SkillQuestion> createTestingJavaQuestions() {
//        List<SkillQuestion> questions = new ArrayList<>();
//
//        // Question 1
//        SkillQuestion q1 = new SkillQuestion();
//        q1.setId("tj1");
//        q1.setText("How comfortable are you with Java syntax and core concepts?");
//        q1.setSkillCategory("Java");
//        q1.setOptions(List.of(
//                "I'm new to Java or have limited knowledge", // BEGINNER
//                "I'm comfortable with most Java features including OOP concepts", // INTERMEDIATE
//                "I have deep knowledge of Java including advanced features" // ADVANCED
//        ));
//        questions.add(q1);
//
//        // Question 2
//        SkillQuestion q2 = new SkillQuestion();
//        q2.setId("tj2");
//        q2.setText("Have you worked with Selenium before?");
//        q2.setSkillCategory("Selenium");
//        q2.setOptions(List.of(
//                "Never used it or only in simple test cases", // BEGINNER
//                "I've created test suites with Selenium", // INTERMEDIATE
//                "I've built comprehensive test frameworks using Selenium" // ADVANCED
//        ));
//        questions.add(q2);
//
//        // Question 3
//        SkillQuestion q3 = new SkillQuestion();
//        q3.setId("tj3");
//        q3.setText("How experienced are you with Behavior-Driven Development (BDD)?");
//        q3.setSkillCategory("BDD");
//        q3.setOptions(List.of(
//                "Limited conceptual understanding", // BEGINNER
//                "I've used BDD frameworks like Cucumber in several projects", // INTERMEDIATE
//                "I've implemented comprehensive BDD testing strategies" // ADVANCED
//        ));
//        questions.add(q3);
//
//        // Question 4
//        SkillQuestion q4 = new SkillQuestion();
//        q4.setId("tj4");
//        q4.setText("How would you rate your knowledge of JUnit?");
//        q4.setSkillCategory("JUnit");
//        q4.setOptions(List.of(
//                "I can write basic unit tests with JUnit", // BEGINNER
//                "I'm comfortable with advanced JUnit features and mocking", // INTERMEDIATE
//                "I have expert-level knowledge of JUnit and testing best practices" // ADVANCED
//        ));
//        questions.add(q4);
//
//        // Question 5
//        SkillQuestion q5 = new SkillQuestion();
//        q5.setId("tj5");
//        q5.setText("How familiar are you with test planning and strategy?");
//        q5.setSkillCategory("Test Planning");
//        q5.setOptions(List.of(
//                "I understand basic test planning concepts", // BEGINNER
//                "I've created test plans for several projects", // INTERMEDIATE
//                "I've designed comprehensive test strategies for complex systems" // ADVANCED
//        ));
//        questions.add(q5);
//
//        return questions;
//    }

    public SurveyDefinition getSurveyDefinition(String email, String targetRole, String language) throws ResourceNotFoundException {
        SurveyDefinition surveyDefinition = surveyDefinitionRepository
                .findByTargetRoleAndLanguageAndActive(targetRole, language, true)
                .orElseThrow(() -> new ResourceNotFoundException("Survey definition not found"));

        ResumeDetails resumeDetails = resumeRepo.findByEmail(email);
        if(resumeDetails==null)
            return surveyDefinition;

        List<SkillQuestion> questions = surveyDefinition.getQuestions();
        Set<String> skills = new HashSet<>(resumeDetails.getSkills());
        for (SkillQuestion question : questions) {
            if (skills.contains(question.getSkillCategory())) {
                question.setFromResume(true);
            }
        }
        return surveyDefinition;
    }

    public SurveyResponse submitSurvey(String userId, SurveySubmissionDTO submission) throws UserNotFoundException, ResourceNotFoundException {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String targetLanguage = submission.getPreferredLanguage();
        String targetRole = submission.getTargetRole();

        userService.setTargetLanguage(userId,targetLanguage);
        userService.setTargetRole(userId,targetRole);

        // Create survey response
        SurveyResponse response = new SurveyResponse();
        response.setId(UUID.randomUUID().toString());
        response.setUserEmail(userId);
        response.setTargetRole(targetRole);
        response.setPreferredLanguage(targetLanguage);
        response.setSkillAssessments(evaluateSkillLevels(submission.getSkillResponses()));
        response.setOverallSkillLevel(determineOverallSkillLevel(response.getSkillAssessments()));
        response.setCompletedAt(new Date());

        // Save and return
        return surveyResponseRepository.save(response);
    }

    private Map<String, String> evaluateSkillLevels(Map<String, Integer> skillResponses) {
        Map<String, String> skillLevels = new HashMap<>();

        for (Map.Entry<String, Integer> entry : skillResponses.entrySet()) {
            String skill = entry.getKey();
            int responseIndex = entry.getValue();

            // Convert response index to skill level
            String skillLevel;
            if (responseIndex == 0) {
                skillLevel = "BEGINNER";
            } else if (responseIndex == 1) {
                skillLevel = "INTERMEDIATE";
            } else {
                skillLevel = "ADVANCED";
            }

            skillLevels.put(skill, skillLevel);
        }

        return skillLevels;
    }

    private String determineOverallSkillLevel(Map<String, String> skillLevels) {
        // Count occurrences of each level
        int beginnerCount = 0;
        int intermediateCount = 0;
        int advancedCount = 0;

        for (String level : skillLevels.values()) {
            switch (level) {
                case "BEGINNER" -> beginnerCount++;
                case "INTERMEDIATE" -> intermediateCount++;
                case "ADVANCED" -> advancedCount++;
            }
        }

        // Determine overall level based on majority
        if (beginnerCount >= intermediateCount && beginnerCount >= advancedCount) {
            return "BEGINNER";
        } else if (intermediateCount >= beginnerCount && intermediateCount >= advancedCount) {
            return "INTERMEDIATE";
        } else {
            return "ADVANCED";
        }
    }

    public Optional<SurveyResponse> getUserSurvey(String userId) {
        return surveyResponseRepository.findByUserEmail(userId);
    }
}