package com.panicatthedebug.pathsync.service;

import com.panicatthedebug.pathsync.exception.UnauthorizedException;
import com.panicatthedebug.pathsync.exception.UserAlreadyExistsException;
import com.panicatthedebug.pathsync.exception.UserNotFoundException;
import com.panicatthedebug.pathsync.exception.ValidationException;
import com.panicatthedebug.pathsync.model.User;
import com.panicatthedebug.pathsync.repository.UserRepository;
import com.panicatthedebug.pathsync.security.JwtService;
import com.panicatthedebug.pathsync.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    static final String MESSAGE = "message";

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    @Autowired
    public UserService(UserRepository userRepo, JwtService jwtService, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
    }

    public Map<String, String> registerUser(String firstName, String lastName, String email, String password) throws UserAlreadyExistsException, ValidationException {
        String validationError = ValidationUtil.validateRegistration(firstName, lastName, email, password);
        if (validationError != null) throw new ValidationException(validationError);

        if (userRepo.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("User already exists.");
        }
        userRepo.save(new User(firstName, lastName, email, passwordEncoder.encode(password), "USER", null, null,false));
        return Map.of(MESSAGE, "User registered successfully.");
    }

    public Map<String, String> loginUser(String email, String password) throws UnauthorizedException, UserNotFoundException, ValidationException {
        String validationError = ValidationUtil.validateLogin(email, password);
        if (validationError != null) throw new ValidationException(validationError);

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Unauthorized request. Please verify credentials and try again.", ex);
        }

        return Map.of("accessToken", jwtService.generateToken(email),
                "username", user.getFirstName() + " " + user.getLastName(),
                "role", user.getRole(),
                "surveyStatus", user.getTargetRole() == null || user.getTargetRole().isBlank()? "INCOMPLETE" : "COMPLETE",
                "assessmentStatus", user.isAssessmentCompleted()? "COMPLETE" : "INCOMPLETE"
        );
    }

    public User getUserByEmail(String name) throws UserNotFoundException {
        User user = userRepo.findByEmail(name);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

    public void setTargetLanguage(String email, String targetLanguage) throws UserNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        user.setTargetLanguage(targetLanguage);
        userRepo.save(user);
    }

    public void setTargetRole(String email, String targetRole) throws UserNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        user.setTargetRole(targetRole);
        userRepo.save(user);
    }

    public void setAssessmentCompleted(String email, String finalSkillLevel) throws UserNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        user.setFinalSkillLevel(finalSkillLevel);
        user.setAssessmentCompleted(true);
        userRepo.save(user);
    }

    public boolean hasAssessmentCompleted(String email) throws UserNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user.isAssessmentCompleted();
    }

    public String getUserSkillLevel(String email) throws UserNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user.getFinalSkillLevel();
    }
}
