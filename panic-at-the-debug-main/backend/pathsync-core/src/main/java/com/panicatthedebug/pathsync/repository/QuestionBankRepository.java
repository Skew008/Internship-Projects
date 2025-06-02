package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.QuestionBankItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankRepository extends MongoRepository<QuestionBankItem, String> {

    @Query("{ 'targetLanguage': ?0, 'difficultyLevel': ?1, 'targetRole': ?2, 'topic': ?3 }")
    List<QuestionBankItem> findByCriteria(String targetLanguage, String difficultyLevel, String targetRole, String topic);


    @Query(value = "{ '_id': ?0 }", fields = "{ 'topic': 1 }")
    QuestionBankItem getTopicById(String id);
}
