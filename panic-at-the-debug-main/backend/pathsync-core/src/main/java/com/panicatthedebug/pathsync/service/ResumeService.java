package com.panicatthedebug.pathsync.service;

import com.panicatthedebug.pathsync.model.ResumeDetails;
import com.panicatthedebug.pathsync.repository.ResumeRepo;
import com.panicatthedebug.pathsync.util.ResumeParser;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepo repo;

    @Autowired
    private ResumeParser resumeParser;

    public ResumeDetails  parseAndSaveResume(MultipartFile file, String email) throws IOException, TikaException {
        try {
            ResumeDetails resumeDetails = resumeParser.parseResume(file, email);

            ResumeDetails existingResume = repo.findByEmail(resumeDetails.getEmail());
            if (existingResume != null) {
                // Update existing resume
                existingResume.setFirstName(resumeDetails.getFirstName());
                existingResume.setLastName(resumeDetails.getLastName());
                existingResume.setSkills(resumeDetails.getSkills());
                return repo.save(existingResume);
            }
            return repo.save(resumeDetails);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse resume: " + e.getMessage(), e);
        }
    }
}
