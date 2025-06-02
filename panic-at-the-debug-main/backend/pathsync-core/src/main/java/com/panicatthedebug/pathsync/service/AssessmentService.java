package com.panicatthedebug.pathsync.service;

import com.panicatthedebug.pathsync.dto.AssessmentDTO;
import com.panicatthedebug.pathsync.exception.*;
import com.panicatthedebug.pathsync.model.*;
import org.springframework.stereotype.Service;

import com.panicatthedebug.pathsync.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final UserService userService;
    private final QuestionBankService questionBankService;
    private final QuestionBankRepository questionBankRepository;

    public AssessmentService(
            AssessmentRepository assessmentRepository,
            SurveyResponseRepository surveyResponseRepository,
            UserService userService,
            QuestionBankService questionBankService, QuestionBankRepository questionBankRepository) {
        this.assessmentRepository = assessmentRepository;
        this.surveyResponseRepository = surveyResponseRepository;
        this.userService = userService;
        this.questionBankService = questionBankService;
        this.questionBankRepository = questionBankRepository;
    }

    /**
     * Generate an assessment based on survey results if the user is intermediate or advanced
     * and hasn't completed an assessment yet
     */
    public Assessment generateAssessmentFromSurvey(String userId) throws UserNotFoundException, InvalidOperationException, ResourceNotFoundException {
        // Check if user has already completed an assessment
        if (userService.hasAssessmentCompleted(userId)) {
            throw new InvalidOperationException("You have already completed your assessment");
        }


        // Get the user's survey response
        SurveyResponse surveyResponse = surveyResponseRepository.findByUserEmail(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found for user"));

        // Check if user already has an incomplete assessment
        Optional<Assessment> existingAssessment = assessmentRepository.findByUserEmailAndCompletedFalse(userId);
        if (existingAssessment.isPresent()) {
            return existingAssessment.get();
        }

        // Map survey role to question bank role format
        String targetRole = surveyResponse.getTargetRole();

        // Create a new assessment
        Assessment assessment = new Assessment();
        assessment.setUserEmail(userId);
        assessment.setTargetRole(targetRole);
        assessment.setPreferredLanguage(surveyResponse.getPreferredLanguage());
        assessment.setSkillLevel(surveyResponse.getOverallSkillLevel());
        assessment.setCreatedAt(new Date());
        assessment.setDurationMinutes(25);
        assessment.setCompleted(false);

        // Select questions from the question bank based on the survey results
        List<Question> selectedQuestions = selectQuestionsFromBank(
                targetRole,
                surveyResponse.getPreferredLanguage(),
                surveyResponse.getSkillAssessments()
        );

        assessment.setQuestions(selectedQuestions);

        // Save and return the assessment
        return assessmentRepository.save(assessment);
    }

    /**
     * Select questions from the question bank based on user's topic-specific skill levels
     * Each topic will have exactly 5 questions
     */
    private List<Question> selectQuestionsFromBank(
            String targetRole,
            String language,
            Map<String, String> skillAssessments) {


        List<Question> selectedQuestions = new ArrayList<>();

        // For each topic in skill assessments, select 5 questions with the correct difficulty level
        for (Map.Entry<String, String> entry : skillAssessments.entrySet()) {
            String skillCategory = entry.getKey();
            String topicDifficultyLevel = entry.getValue();

            System.out.println(skillCategory);
            // Map skill category to topic
            String topic = mapSkillCategoryToTopic(skillCategory);
            System.out.println(topic);

            // Find questions in the bank for this topic with the appropriate difficulty level
            List<QuestionBankItem> bankItems = questionBankService.retrieveQuestions(
                    language,
                    topicDifficultyLevel,
                    targetRole,
                    topic
            );

            // Convert bank items to assessment questions
            for (QuestionBankItem item : bankItems) {
                Question question = convertBankItemToQuestion(item);
                selectedQuestions.add(question);
            }
        }

        return selectedQuestions;
    }

    /**
     * Map a single skill category to a topic
     */
    private String mapSkillCategoryToTopic(String skillCategory) {
        Map<String, String> mappings = Map.ofEntries(
                Map.entry("bj1","Java"),
                Map.entry("bj2", "Spring Boot"),
                Map.entry("bj3","REST APIs"),
                Map.entry("bj4","SQL"),
                Map.entry("bj5","Microservices"),
                Map.entry("fj1","JavaScript"),
                Map.entry("fj2","React"),
                Map.entry("fj3","Node.js"),
                Map.entry("fj4","HTML/CSS"),
                Map.entry("fj5","State Management"),
                Map.entry("tj1", "Java"),
                Map.entry("tj2","Selenium"),
                Map.entry("tj3","BDD"),
                Map.entry("tj4","JUnit"),
                Map.entry("tj5","Test Planning")

        );

        return mappings.getOrDefault(skillCategory, skillCategory);
    }

    /**
     * Convert a question bank item to an assessment question
     */
    private Question convertBankItemToQuestion(QuestionBankItem item) {
        Question question = new Question();
        question.setId(item.getId());
        question.setText(item.getText());
        question.setOptions(item.getOptions());
        question.setCorrectOptionIndex(item.getCorrectOptionIndex());
        question.setExplanation(item.getExplanation());
        question.setSkillCategory(item.getTopic()); // Use topic as skill category
        return question;
    }

    /**
     * Start an assessment
     */
    public Assessment startAssessment(String assessmentId, String userId) throws AccessDeniedException, InvalidOperationException, ResourceNotFoundException {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        // Verify the assessment belongs to the user
        if (!assessment.getUserEmail().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to access this assessment");
        }

        // Check if assessment is already completed
        if (assessment.isCompleted()) {
            throw new InvalidOperationException("This assessment is already completed");
        }

        // Check if assessment is already started
        if (assessment.getStartedAt() != null) {
            // Return the existing assessment with time remaining
            return assessment;
        }

        // Start the assessment
        assessment.setStartedAt(new Date());
        return assessmentRepository.save(assessment);
    }

    /**
     * Complete an assessment and calculate the score
     * Also update the user's final skill level based on assessment results
     */
    public Map<String,Object> completeAssessment(String assessmentId, String userId) throws UserNotFoundException, AccessDeniedException, ResourceNotFoundException, InvalidOperationException {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        // Verify the assessment belongs to the user
        if (!assessment.getUserEmail().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to access this assessment");
        }

        // Check if assessment is already completed
        if (assessment.isCompleted()) {
            return Map.of("ResultMap",generateQuestionResultMap(assessment.getQuestions()),"Score", String.valueOf(assessment.getScore()));
        }

        // Check if assessment has started
        if (assessment.getStartedAt() == null) {
            throw new InvalidOperationException("Assessment has not been started");
        }

        // Mark as completed
        assessment.setCompleted(true);
        assessment.setCompletedAt(new Date());

        // Calculate score
        int correctAnswers = 0;
        for (Question question : assessment.getQuestions()) {
            if (question.getUserSelectedOption() != null &&
                    question.getUserSelectedOption() == question.getCorrectOptionIndex()) {
                correctAnswers++;
            }
        }

        int totalQuestions = assessment.getQuestions().size();
        int score = totalQuestions > 0 ? (correctAnswers * 100) / totalQuestions : 0;
        assessment.setScore(score);

        // Determine final skill level based on assessment score
        String finalSkillLevel = determineFinalSkillLevel(score, assessment.getSkillLevel());

        // Update user's assessment status and final skill level
        userService.setAssessmentCompleted(userId, finalSkillLevel);

        // Save and return the updated assessment
        assessmentRepository.save(assessment);
        return Map.of("ResultMap", generateQuestionResultMap(assessment.getQuestions()),"Score", String.valueOf(score));
    }

    /**
     * Determine the final skill level based on assessment score and initial survey level
     */
    private String determineFinalSkillLevel(int score, String surveySkillLevel) {
        if (score < 40) {
            return "BEGINNER";
        } else if (score < 70) {
            return "INTERMEDIATE";
        } else {
            return "ADVANCED";
        }
    }

    /**
     * Get time remaining for an assessment in seconds
     */
    public long getTimeRemainingSeconds(Assessment assessment) {
        if (assessment.getStartedAt() == null || assessment.isCompleted()) {
            return 0;
        }

        Date now = new Date();
        Date expiryTime = new Date(assessment.getStartedAt().getTime() +
                (assessment.getDurationMinutes() * 60 * 1000L));

        long remainingMillis = expiryTime.getTime() - now.getTime();
        return Math.max(0, remainingMillis / 1000);
    }

    /**
     * Get a map of question results (CORRECT/INCORRECT) for a completed assessment
     */
    public Map<String, String> generateQuestionResultMap(List<Question> questions) {
        Map<String, String> resultMap = new HashMap<>();

        for (Question question : questions) {
            String result = "INCORRECT";

            // If the user selected the correct option, mark as correct
            if (question.getUserSelectedOption() != null &&
                    question.getUserSelectedOption() == question.getCorrectOptionIndex()) {
                result = "CORRECT";
            }

            resultMap.put(question.getId(), result);
        }

        return resultMap;
    }

    public List<AssessmentDTO> getUserAssessments(String userId) {
        List<Assessment> assessments = assessmentRepository.findByUserEmail(userId);

        // Convert each Assessment to an AssessmentDTO
        return assessments.stream()
                .map(assessment -> {
                    long timeRemaining = getTimeRemainingSeconds(assessment);
                    return AssessmentDTO.from(assessment, timeRemaining);
                })
                .collect(Collectors.toList());
    }

    /**
     * This method is used to analyze the test results and return the proficiency level for each topic.
     *
     * @param questionResults A map containing the question results.
     * @return A map containing the proficiency level for each topic.
     * @throws QuestionNotFoundException if a question ID is not found in the database.
     */
    public Map<String, String> analyzeTestResults(Map<String, String> questionResults) throws QuestionNotFoundException {
        Map<String, Integer> correctAnswersByTopic = new HashMap<>();
        Map<String, Integer> totalQuestionsByTopic = new HashMap<>();

        for (String questionId : questionResults.keySet()) {
            QuestionBankItem questionBankItem = questionBankRepository.getTopicById(questionId);
            if (questionBankItem == null) {
                throw new QuestionNotFoundException("Question ID not found: " + questionId);
            }
            String topic = questionBankItem.getTopic();
            totalQuestionsByTopic.put(topic, totalQuestionsByTopic.getOrDefault(topic, 0) + 1);
            if (questionResults.get(questionId).equalsIgnoreCase("correct")) {
                correctAnswersByTopic.put(topic, correctAnswersByTopic.getOrDefault(topic, 0) + 1);
            }
        }

        Map<String, String> proficiencyByTopic = new HashMap<>();
        for (String topic : totalQuestionsByTopic.keySet()) {
            int correct = correctAnswersByTopic.getOrDefault(topic, 0);
            int total = totalQuestionsByTopic.get(topic);
            double accuracy = (double) correct / total;

            if (accuracy >= 0.8) {
                proficiencyByTopic.put(topic, "Expert");
            } else if (accuracy >= 0.5) {
                proficiencyByTopic.put(topic, "Intermediate");
            } else {
                proficiencyByTopic.put(topic, "Beginner");
            }
        }

        return proficiencyByTopic;
    }

    /**
     * This method is used to get the proficiency of a user by topic.
     *
     * @param questionOutcomes A map containing the question results.
     * @return A map containing the proficiency level for each topic.
     * @throws QuestionNotFoundException if a question ID is not found in the database.
     */
    public Map<String, String> getProficiencyByTopic(Map<String, Map<String, String>> questionOutcomes) throws QuestionNotFoundException {
        return analyzeTestResults(questionOutcomes.get("questionResults"));
    }
}
