package com.examcontrol.controller;

import com.examcontrol.dto.LoginRequest;
import com.examcontrol.dto.LoginResponse;
import com.examcontrol.model.User;
import com.examcontrol.repository.UserRepository;
import com.examcontrol.security.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername()).orElse(null);
        if (user == null || !BCrypt.checkpw(req.getPassword(), user.getPasswordHash()))
            return ResponseEntity.status(401).body("Invalid username or password");
        if (!user.isActive())
            return ResponseEntity.status(403).body("Account is disabled");
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(),
                user.getFullName(), user.getRole().name(), user.getId()));
    }
}
