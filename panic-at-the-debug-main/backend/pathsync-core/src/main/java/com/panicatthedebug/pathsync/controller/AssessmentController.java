package com.panicatthedebug.pathsync.controller;

import com.panicatthedebug.pathsync.dto.AssessmentDTO;
import com.panicatthedebug.pathsync.exception.AccessDeniedException;
import com.panicatthedebug.pathsync.exception.InvalidOperationException;
import com.panicatthedebug.pathsync.exception.ResourceNotFoundException;
import com.panicatthedebug.pathsync.exception.UserNotFoundException;
import com.panicatthedebug.pathsync.model.Assessment;
import com.panicatthedebug.pathsync.repository.SurveyResponseRepository;
import com.panicatthedebug.pathsync.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final UserService userService;
    private final SurveyResponseRepository surveyResponseRepository;

    public AssessmentController(
            AssessmentService assessmentService,
            UserService userService,
            SurveyResponseRepository surveyResponseRepository) {
        this.assessmentService = assessmentService;
        this.userService = userService;
        this.surveyResponseRepository = surveyResponseRepository;
    }

//    /**
//     * Check if user is eligible for assessment and hasn't completed one yet
//     */
//    @GetMapping("/eligibility")
//    public ResponseEntity<?> checkAssessmentEligibility(Principal principal) {
//        String userId = principal.getName();
//
//        // Check if user has already completed an assessment
//        if (userService.hasAssessmentCompleted(userId)) {
//            String skillLevel = userService.getUserSkillLevel(userId);
//            return ResponseEntity.ok(Map.of(
//                    "eligible", false,
//                    "message", "You have already completed your assessment",
//                    "skillLevel", skillLevel
//            ));
//        }
//
//        // Check if user has completed the survey
//        if (!userService.hasSurveyCompleted(userId)) {
//            return ResponseEntity.ok(Map.of(
//                    "eligible", false,
//                    "message", "Please complete the survey first",
//                    "nextStep", "SURVEY"
//            ));
//        }
//
//        // Get the survey response to check if all topics are beginner level
//        SurveyResponse surveyResponse = surveyResponseRepository.findByUserId(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("Survey not found for user"));
//
//        boolean allBeginner = surveyResponse.getSkillAssessments().values().stream()
//                .allMatch(level -> "BEGINNER".equals(level));
//
//        if (allBeginner) {
//            return ResponseEntity.ok(Map.of(
//                    "eligible", false,
//                    "message", "Based on your survey, you'll be directed to beginner content without an assessment",
//                    "skillLevel", "BEGINNER"
//            ));
//        }
//
//        try {
//            // Try to generate an assessment
//            Assessment assessment = assessmentService.generateAssessmentFromSurvey(userId);
//            return ResponseEntity.ok(Map.of(
//                    "eligible", true,
//                    "message", "You are eligible to take the assessment",
//                    "assessmentId", assessment.getId()
//            ));
//        } catch (InvalidOperationException e) {
//            // Other error
//            return ResponseEntity.ok(Map.of(
//                    "eligible", false,
//                    "message", e.getMessage()
//            ));
//        }
//    }

    /**
     * Generate an assessment based on survey results
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateAssessment(Authentication authentication) throws UserNotFoundException, ResourceNotFoundException {
        String userId = authentication.getName();

        // Check if user has already completed an assessment
        if (userService.hasAssessmentCompleted(userId)) {
            String skillLevel = userService.getUserSkillLevel(userId);
            return ResponseEntity.ok(Map.of(
                    "message", "You have already completed your assessment",
                    "skillLevel", skillLevel,
                    "nextStep", "LEARNING_PATH"
            ));
        }

        try {
            Assessment assessment = assessmentService.generateAssessmentFromSurvey(userId);
            return ResponseEntity.ok(assessment);
        } catch (InvalidOperationException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all assessments for the current user
     */
    @GetMapping
    public ResponseEntity<List<AssessmentDTO>> getUserAssessments(Authentication authentication) {
        String userId = authentication.getName();
        List<AssessmentDTO> assessments = assessmentService.getUserAssessments(userId);

        return ResponseEntity.ok(assessments);
    }

//    /**
//     * Get a specific assessment
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<AssessmentDTO> getAssessment(@PathVariable String id, Authentication authentication) {
//        String userId = authentication.getName();
//        Assessment assessment = assessmentService.getAssessment(id, userId);
//
//        // Calculate time remaining
//        long timeRemaining = assessmentService.getTimeRemainingSeconds(assessment);
//
//        AssessmentDTO dto = AssessmentDTO.from(assessment, timeRemaining);
//
//        // If assessment is completed, include answers and explanations
//        if (assessment.isCompleted()) {
//            for (int i = 0; i < assessment.getQuestions().size(); i++) {
//                Question question = assessment.getQuestions().get(i);
//                QuestionDTO questionDto = dto.getQuestions().get(i);
//
//                questionDto.setCorrectOptionIndex(question.getCorrectOptionIndex());
//                questionDto.setExplanation(question.getExplanation());
//            }
//        }
//
//        return ResponseEntity.ok(dto);
//    }

    /**
     * Start an assessment
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<AssessmentDTO> startAssessment(@PathVariable String id, Authentication authentication) throws AccessDeniedException, InvalidOperationException, ResourceNotFoundException {
        String userId = authentication.getName();
        Assessment assessment = assessmentService.startAssessment(id, userId);

        // Calculate time remaining
        long timeRemaining = assessmentService.getTimeRemainingSeconds(assessment);

        return ResponseEntity.ok(AssessmentDTO.from(assessment, timeRemaining));
    }

    /**
     * Complete an assessment
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeAssessment(@PathVariable String id, Authentication authentication) throws UserNotFoundException, AccessDeniedException, InvalidOperationException, ResourceNotFoundException {
        String userId = authentication.getName();
        Map<String, Object> resultMap = assessmentService.completeAssessment(id, userId);

        Map<String, String> questionResultMap = (Map<String, String>) resultMap.get("ResultMap");
        // Get the final skill level
        String finalSkillLevel = userService.getUserSkillLevel(userId);
        int score = Integer.parseInt((String) resultMap.get("Score"));

        return ResponseEntity.ok(Map.of(
                "score", score,
                "finalSkillLevel", finalSkillLevel,
                "nextStep", "LEARNING_PATH",
                "message", "Assessment completed. You will now be directed to your personalized learning path.",
                "questionResults", questionResultMap  // Question result map for learning path
        ));
    }
}