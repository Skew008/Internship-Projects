package com.panicatthedebug.pathsync.controller;

import com.panicatthedebug.pathsync.exception.UnauthorizedException;
import com.panicatthedebug.pathsync.exception.UserAlreadyExistsException;
import com.panicatthedebug.pathsync.exception.UserNotFoundException;
import com.panicatthedebug.pathsync.exception.ValidationException;
import com.panicatthedebug.pathsync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody Map<String, String> userData) throws UserAlreadyExistsException, ValidationException {
        return ResponseEntity.ok(userService.registerUser(
                userData.get("firstName"),
                userData.get("lastName"),
                userData.get("email"),
                userData.get("password")
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> loginData) throws UnauthorizedException, UserNotFoundException, ValidationException {
        return ResponseEntity.ok(userService.loginUser(
                loginData.get("email"),
                loginData.get("password")
        ));
    }
}
