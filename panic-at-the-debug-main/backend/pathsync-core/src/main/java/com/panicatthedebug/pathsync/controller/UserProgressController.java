package com.panicatthedebug.pathsync.controller;

import com.panicatthedebug.pathsync.model.UserProgress;
import com.panicatthedebug.pathsync.service.UserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user-progress")
public class UserProgressController {

    @Autowired
    private UserProgressService userProgressService;

    /**
     * API to get the overall progress of a user's learning path.
     *
     * @return A response entity with the overall progress metrics.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics(Authentication authentication) {
        Map<String, Object> metrics = userProgressService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }

    /**
     * API to get the upcoming due dates for a user's learning path.
     *
     * @return A response entity with the list of upcoming due dates.
     */
    @GetMapping("/dashboard/upcoming-due-dates")
    public ResponseEntity<List<UserProgress.ProgressEntry>> getUpcomingDueDates(Authentication authentication) {
        List<UserProgress.ProgressEntry> upcomingDueDates = userProgressService.getUpcomingDueDates(authentication.getName());
        return ResponseEntity.ok(upcomingDueDates);
    }

    /**
     * API to get the major skills (primary topics) for a user's learning path.
     *
     * @return A response entity with the list of major skills.
     */
    @GetMapping("/dashboard/major-skills")
    public ResponseEntity<List<String>> getMajorSkills(Authentication authentication) {
        List<String> majorSkills = userProgressService.getMajorSkills(authentication.getName());
        return ResponseEntity.ok(majorSkills);
    }

    /**
     * API to mark a subtopic as completed for a user.
     *
     * @param topicName     The topic name.
     * @param subTopicName  The subtopic name.
     * @return A response entity indicating success.
     */
    @PostMapping("/mark-completed")
    public ResponseEntity<Void> markSubTopicAsCompleted(
            Authentication authentication,
            @RequestParam String topicName,
            @RequestParam String subTopicName) {
        userProgressService.markSubTopicAsCompleted(authentication.getName(), topicName, subTopicName);
        return ResponseEntity.ok().build();
    }

    /**
     * API to get all completed tasks for a user.
     *
     * @return A response entity with the list of completed tasks.
     */
    @GetMapping("/completed-tasks")
    public ResponseEntity<List<UserProgress.ProgressEntry>> getCompletedTasks(Authentication authentication) {
        List<UserProgress.ProgressEntry> completedTasks = userProgressService.getCompletedTasks(authentication.getName());
        return ResponseEntity.ok(completedTasks);
    }

    /**
     * API to get all pending tasks for a user.
     *
     * @return A response entity with the list of pending tasks.
     */
    @GetMapping("/pending-tasks")
    public ResponseEntity<List<UserProgress.ProgressEntry>> getPendingTasks(Authentication authentication) {
        List<UserProgress.ProgressEntry> pendingTasks = userProgressService.getPendingTasks(authentication.getName());
        return ResponseEntity.ok(pendingTasks);
    }
}
