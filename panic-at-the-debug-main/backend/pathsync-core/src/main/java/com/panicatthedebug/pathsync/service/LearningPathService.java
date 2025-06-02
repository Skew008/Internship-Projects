package com.panicatthedebug.pathsync.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panicatthedebug.pathsync.exception.LearnPathAlreadyExistsException;
import com.panicatthedebug.pathsync.exception.LearnPathNotFoundException;
import com.panicatthedebug.pathsync.exception.SurveyNotCompleteException;
import com.panicatthedebug.pathsync.model.CustomLearningPath;
import com.panicatthedebug.pathsync.model.StandardLearningPath;
import com.panicatthedebug.pathsync.model.User;
import com.panicatthedebug.pathsync.model.UserProgress;
import com.panicatthedebug.pathsync.repository.CustomLearningPathRepository;
import com.panicatthedebug.pathsync.repository.StandardLearningPathRepository;
import com.panicatthedebug.pathsync.repository.UserProgressRepository;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LearningPathService {
    private final ChatClient chatClient;
    private final StandardLearningPathRepository standardLearningPathRepository;
    private final CustomLearningPathRepository customLearningPathRepository;
    private final UserProgressRepository userProgressRepository;
    private final ObjectMapper objectMapper;
    private final Logger logger;

    public LearningPathService(ChatClient chatClient, StandardLearningPathRepository standardLearningPathRepository, CustomLearningPathRepository customLearningPathRepository, UserProgressRepository userProgressRepository) {
        this.chatClient = chatClient;
        this.standardLearningPathRepository = standardLearningPathRepository;
        this.customLearningPathRepository = customLearningPathRepository;
        this.userProgressRepository = userProgressRepository;
        this.logger = LoggerFactory.getLogger(LearningPathService.class);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates a customized learning path for the user based on their proficiency levels and persists it.
     *
     * @param user               The user details (target role and language).
     * @param proficiencyByTopic A map of topics and their corresponding proficiency levels.
     * @return A customized learning path as a list of topics.
     */
    public CustomLearningPath createCustomLearningPath(User user, Map<String, String> proficiencyByTopic) throws SurveyNotCompleteException {
        if (user.getTargetRole() == null || user.getTargetRole().isBlank()
                || user.getTargetLanguage() == null
                || user.getTargetLanguage().isBlank())
            throw new SurveyNotCompleteException("Please complete survey before trying to access learn path.");
        StandardLearningPath standardLearningPath = standardLearningPathRepository
                .findByTargetRoleAndTargetLanguage(user.getTargetRole(), user.getTargetLanguage())
                .orElseThrow(() -> new RuntimeException("No standard learning path found for the given role and language."));

        String proficiencyJson;
        try {
            proficiencyJson = objectMapper.writeValueAsString(proficiencyByTopic);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize proficiency data", e);
        }

        String systemMessage = """
                    You are an expert learning path generator for software engineers.
                    Your task is to create a customized learning path based on the user's target role, target language, and proficiency levels.
                    The learning path should focus on weak areas and skip topics or subtopics within topics the user is already proficient in.
                    The proficiency levels are provided in JSON format, where the keys are the topics and the values are the proficiency levels (e.g., "beginner", "intermediate", "advanced").
                    The generated learning path should contain a map of topics in the order they should be learned. Each topic can have multiple subtopics mapped to the parent topic.
                    Each subtopic is also a map consisting of 3 keys, "duration", "completionStatus", and "learningResources".
                    Duration indicates the time required to complete the subtopic, completionStatus indicates whether the subtopic is completed or not (default is NOT_STARTED), and learningResources is also a map of resources to learn the subtopic.
                    The key in the learningResources map denotes the resource name while the value contains link to the resource. The learning resources can be a mix of videos, articles, official documentation etc.
                    Return the learning path as a JSON object of topics.
                """;

        String userMessage = buildUserMessage(user, proficiencyJson, standardLearningPath);

        Prompt prompt = new Prompt(new SystemMessage(systemMessage), new UserMessage(userMessage));

        JSONObject jsonObject = chatClient.prompt(prompt).call().entity(JSONObject.class);
        CustomLearningPath customLearningPath = mapToCustomLearningPath(jsonObject);
        customLearningPath.setId(user.getEmail());
        saveCustomLearningPath(customLearningPath);
        return customLearningPath;
    }

    /**
     * Builds the user message for the prompt.
     *
     * @param user                 The user details (target role and language).
     * @param proficiencyJson      The serialized proficiency data in JSON format.
     * @param standardLearningPath The standard learning path fetched from the database.
     * @return The user message as a string.
     */
    private String buildUserMessage(User user, String proficiencyJson, StandardLearningPath standardLearningPath) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append("User Details:\n");
        userMessage.append("- Target Role: ").append(user.getTargetRole()).append("\n");
        userMessage.append("- Target Language: ").append(user.getTargetLanguage()).append("\n");
        userMessage.append("Proficiency Levels (in JSON format):\n");
        userMessage.append(proficiencyJson).append("\n");
        userMessage.append("Standard Learning Path for ").append(user.getTargetRole()).append(" in ")
                .append(user.getTargetLanguage()).append(":\n");

        List<String> topics = standardLearningPath.getTopics();
        for (int i = 0; i < topics.size(); i++) {
            userMessage.append(i + 1).append(". ").append(topics.get(i)).append("\n");
        }

        userMessage.append("Customize the learning path by focusing on weak areas and skipping topics the user is already proficient in.\n");
        userMessage.append("You can also expand on the standard learning path by adding more advanced topics or subtopics if required based on the user's proficiency levels.");
        return userMessage.toString();
    }

    /**
     * Saves the given learning path to the MongoDB database.
     * <p>
     * This method takes a LearningPath object and persists it in the custom_learning_paths collection
     * in MongoDB. If a document with the same ID already exists, it will be updated; otherwise,
     * a new document will be created.
     *
     * @param customLearningPath The LearningPath object to be saved. This object contains the hierarchical
     *                           structure of topics, subtopics, and their associated details such as duration,
     *                           completion status, and learning resources.
     */
    public void saveCustomLearningPath(CustomLearningPath customLearningPath) {
        customLearningPathRepository.save(customLearningPath);
    }

    /**
     * Retrieves a custom learning path for the given user email.
     *
     * @param email              The email of the user.
     * @return The custom learning path for the user.
     * @throws LearnPathNotFoundException If no custom learning path is found for the given email.
     */
    public CustomLearningPath getCustomLearningPath(String email) throws LearnPathNotFoundException {
        return customLearningPathRepository.findById(email)
                .orElseThrow(() -> new LearnPathNotFoundException("No custom learning path found for email: " + email));
    }

    /**
     * Maps a JSON object to a CustomLearningPath object.
     *
     * @param json The JSON object to be mapped.
     * @return The mapped CustomLearningPath object.
     */
    public CustomLearningPath mapToCustomLearningPath(JSONObject json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json.toJSONString(), CustomLearningPath.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Failed to map JSON to CustomLearningPath", e);
        }
    }

    /**
     * Adds a new standard learning path to the database.
     *
     * @param standardLearningPath The standard learning path to be added.
     * @return A message indicating the success of the operation.
     */
    public Map<String, String> addStandardLearningPath(StandardLearningPath standardLearningPath) throws LearnPathAlreadyExistsException {
        if(standardLearningPathRepository.existsById(standardLearningPath.getId()))
            throw new LearnPathAlreadyExistsException( "A standard learning path with this id already exists.");
        StandardLearningPath savedLearningPath = standardLearningPathRepository.save(standardLearningPath);
        return Map.of("message", "Standard learning path added successfully.");
    }

    /**
     * Checks if a custom learning path with the given name already exists.
     *
     * @param name The name of the custom learning path.
     * @throws LearnPathAlreadyExistsException If a custom learning path with the given name already exists.
     */
    public void checkCustomLearnPathAlreadyExists(String name) throws LearnPathAlreadyExistsException {
        if (customLearningPathRepository.existsById(name)) {
            throw new LearnPathAlreadyExistsException("Custom learn path with this id already exists.");
        }
    }

    /**
     * Starts the learning path for a user by populating the progress tracking table.
     *
     * @param userEmail       The user's email.
     */
    public void startLearningPath(String userEmail) {
        CustomLearningPath customLearningPath = customLearningPathRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("Learning path not found"));

        List<UserProgress.ProgressEntry> progressEntries = new ArrayList<>();
        for (Map.Entry<String, CustomLearningPath.Topic> topicEntry : customLearningPath.getTopics().entrySet()) {
            String topicName = topicEntry.getKey();
            CustomLearningPath.Topic topic = topicEntry.getValue();

            for (Map.Entry<String, CustomLearningPath.SubTopic> subTopicEntry : topic.getSubTopics().entrySet()) {
                String subTopicName = subTopicEntry.getKey();
                CustomLearningPath.SubTopic subTopic = subTopicEntry.getValue();

                int durationInHours = Integer.parseInt(subTopic.getDuration().replace(" hours", ""));
                int daysRequired = (int) Math.ceil((double) durationInHours / 6);
                String estimatedCompletionDate = LocalDate.now().plusDays(daysRequired).toString();

                progressEntries.add(new UserProgress.ProgressEntry(
                        topicName,
                        subTopicName,
                        estimatedCompletionDate,
                        false,
                        subTopic.getDuration()
                ));
            }
        }

        // Save the progress entries to the database
        UserProgress userProgress = new UserProgress(
                userEmail,
                progressEntries,
                0
        );
        userProgressRepository.save(userProgress);
    }
}