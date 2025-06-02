package com.panicatthedebug.pathsync.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panicatthedebug.pathsync.model.QuestionBankItem;
import com.panicatthedebug.pathsync.repository.QuestionBankRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class QuestionBankService {

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private ChatClient openAiChatClient;

    public List<QuestionBankItem> retrieveQuestions(String targetLanguage, String difficultyLevel, String targetRole, String topic) {
        List<QuestionBankItem> allQuestions = questionBankRepository.findByCriteria(targetLanguage, difficultyLevel, targetRole, topic);

        // Select questions based on least usage count and last used date
        List<QuestionBankItem> selectedQuestions = pickQuestions(allQuestions);

        for (QuestionBankItem question : selectedQuestions) {
            question.setUsageCount(question.getUsageCount() + 1);
            question.setLastUsedAt(new Date());
        }

        // Save the updated questions back to the database
        questionBankRepository.saveAll(selectedQuestions);

        // Return the selected questions
        return rephrase(selectedQuestions);
    }

    public void saveQuestion(QuestionBankItem question) {
        question.setCreatedAt(new Date());
        question.setUsageCount(0);
        questionBankRepository.save(question);
    }

    private List<QuestionBankItem> rephrase(List<QuestionBankItem> questions) {

        for(QuestionBankItem question : questions) {
            System.out.println(question.getText());
            Prompt prompt = new Prompt(new UserMessage("Rephrase the following question: " + question.getText()));
            String rephrasedQuestion = openAiChatClient.prompt(prompt).call().content();
            question.setText(rephrasedQuestion);
            System.out.println();
            System.out.println(rephrasedQuestion);
        }

        return questions;
    }

    private List<QuestionBankItem> pickQuestions(List<QuestionBankItem> questions) {
        ObjectMapper objectMapper =  new ObjectMapper();
        String questionsJson;
        try {
            questionsJson = objectMapper.writeValueAsString(questions);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize questions", e);
        }

        String systemMessage = """
            You are a helpful assistant.
            You will receive a list of questions in JSON format.
            Randomly pick any 2 questions from the list and return them as a JSON array in the **same format**.
        """;

        String userMessage = "Here is the list:\n" + questionsJson;

        Prompt prompt = new Prompt(new SystemMessage(systemMessage), new UserMessage(userMessage));
        return openAiChatClient.prompt(prompt).call().entity(new ParameterizedTypeReference<>() {});
    }
}
