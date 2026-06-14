package com.examcontrol.controller;

import com.examcontrol.model.Role;
import com.examcontrol.model.User;
import com.examcontrol.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAll() { return userRepository.findAll(); }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserRequest req) {
        if (userRepository.existsByUsername(req.username))
            return ResponseEntity.badRequest().body("Username already exists");
        if (userRepository.existsByEmail(req.email))
            return ResponseEntity.badRequest().body("Email already exists");
        User user = new User();
        user.setUsername(req.username);
        user.setPasswordHash(BCrypt.hashpw(req.password, BCrypt.gensalt(12)));
        user.setFullName(req.fullName);
        user.setEmail(req.email);
        user.setRole(Role.valueOf(req.role));
        user.setActive(true);
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody UserRequest req) {
        return userRepository.findById(id).map(user -> {
            user.setFullName(req.fullName);
            user.setEmail(req.email);
            user.setRole(Role.valueOf(req.role));
            user.setActive(req.active);
            if (req.password != null && !req.password.isBlank())
                user.setPasswordHash(BCrypt.hashpw(req.password, BCrypt.gensalt(12)));
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    static class UserRequest {
        public String username;
        public String password;
        public String fullName;
        public String email;
        public String role;
        public boolean active = true;
    }
}
