package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.SkillMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillMappingRepo extends MongoRepository<SkillMapping, String > {
}
