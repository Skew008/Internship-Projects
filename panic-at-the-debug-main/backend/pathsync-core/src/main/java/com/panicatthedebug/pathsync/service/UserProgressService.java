package com.panicatthedebug.pathsync.service;


import com.panicatthedebug.pathsync.model.CustomLearningPath;
import com.panicatthedebug.pathsync.model.UserProgress;
import com.panicatthedebug.pathsync.repository.CustomLearningPathRepository;
import com.panicatthedebug.pathsync.repository.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserProgressService {

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private CustomLearningPathRepository customLearningPathRepository;

    /**
     * Retrieves the overall progress metrics for a user's learning path.
     *
     * @param userEmail The user's email.
     * @return A map containing the overall progress metrics.
     */
    public Map<String, Object> getDashboardMetrics(String userEmail) {
        UserProgress userProgress = userProgressRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User progress not found"));

        int totalSubTopics = userProgress.getProgressEntries().size();
        int completedSubTopics = (int) userProgress.getProgressEntries().stream()
                .filter(UserProgress.ProgressEntry::getCompletionStatus)
                .count();

        String estimatedCompletionDate = userProgress.getProgressEntries().stream()
                .map(UserProgress.ProgressEntry::getEstimatedCompletionDate)
                .max(String::compareTo)
                .orElse("N/A");

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalSubTopics", totalSubTopics);
        metrics.put("completedSubTopics", completedSubTopics);
        metrics.put("completionPercentage", (completedSubTopics * 100) / totalSubTopics);
        metrics.put("estimatedCompletionDate", estimatedCompletionDate);

        return metrics;
    }

    /**
     * Retrieves the upcoming due dates for a user's learning path.
     *
     * @param userEmail The user's email.
     * @return A list of progress entries with upcoming due dates.
     */
    public List<UserProgress.ProgressEntry> getUpcomingDueDates(String userEmail) {
        UserProgress userProgress = userProgressRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User progress not found"));

        return userProgress.getProgressEntries().stream()
                .filter(entry -> !entry.getCompletionStatus()) // Not completed
                .sorted(Comparator.comparing(UserProgress.ProgressEntry::getEstimatedCompletionDate))
                .limit(5) // Get the next 5 due dates
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the major skills (primary topics) for a user's learning path.
     *
     * @param userEmail The user's email.
     * @return A list of major skills (topics).
     */
    public List<String> getMajorSkills(String userEmail) {
        UserProgress userProgress = userProgressRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User progress not found"));

        return userProgress.getProgressEntries().stream()
                .map(UserProgress.ProgressEntry::getTopicName)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Marks a specific subtopic as completed for a user.
     *
     * @param userEmail     The user's email.
     * @param topicName     The topic name.
     * @param subTopicName  The subtopic name.
     */
    public void markSubTopicAsCompleted(String userEmail, String topicName, String subTopicName) {
        UserProgress userProgress = userProgressRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User progress not found"));

        // Find the subtopic and mark it as completed
        boolean updated = false;
        for (UserProgress.ProgressEntry entry : userProgress.getProgressEntries()) {
            if (entry.getTopicName().equals(topicName) && entry.getSubTopicName().equals(subTopicName)) {
                entry.setCompletionStatus(true);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new RuntimeException("Subtopic not found");
        }

        // Recalculate the overall completion percentage
        int totalSubTopics = userProgress.getProgressEntries().size();
        int completedSubTopics = (int) userProgress.getProgressEntries().stream()
                .filter(UserProgress.ProgressEntry::getCompletionStatus)
                .count();
        userProgress.setCompletionPercentage((completedSubTopics * 100) / totalSubTopics);

        // Save the updated progress
        userProgressRepository.save(userProgress);

        // Fetch the CustomLearningPath for the user
        CustomLearningPath customLearningPath = customLearningPathRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("Custom learning path not found"));

        // Find the subtopic in CustomLearningPath and mark it as completed
        CustomLearningPath.Topic topic = customLearningPath.getTopics().get(topicName);
        if (topic == null) {
            throw new RuntimeException("Topic not found in CustomLearningPath");
        }

        CustomLearningPath.SubTopic subTopic = topic.getSubTopics().get(subTopicName);
        if (subTopic == null) {
            throw new RuntimeException("Subtopic not found in CustomLearningPath");
        }

        // Update the completionStatus in CustomLearningPath
        subTopic.setCompletionStatus("completed");

        // Save the updated CustomLearningPath
        customLearningPathRepository.save(customLearningPath);
    }

    /**
     * Retrieves all completed subtopics for a user.
     *
     * @param userEmail The user's email.
     * @return A list of completed progress entries.
     */
    public List<UserProgress.ProgressEntry> getCompletedTasks(String userEmail) {
        UserProgress userProgress = userProgressRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User progress not found"));

        return userProgress.getProgressEntries().stream()
                .filter(UserProgress.ProgressEntry::getCompletionStatus)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all pending subtopics for a user.
     *
     * @param userEmail The user's email.
     * @return A list of pending progress entries.
     */
    public List<UserProgress.ProgressEntry> getPendingTasks(String userEmail) {
        UserProgress userProgress = userProgressRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User progress not found"));

        return userProgress.getProgressEntries().stream()
                .filter(entry -> !entry.getCompletionStatus())
                .collect(Collectors.toList());
    }


}
