package com.panicatthedebug.pathsync.controller;

import com.panicatthedebug.pathsync.model.ResumeDetails;
import com.panicatthedebug.pathsync.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;


    @PostMapping("upload")
    public ResponseEntity<ResumeDetails> uploadResume(Authentication authentication, MultipartFile file) {
        try {
            ResumeDetails resumeDetails = resumeService.parseAndSaveResume(file, authentication.getName());
            return ResponseEntity.ok(resumeDetails);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
