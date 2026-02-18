package com.example.price_comparator.controller;

import com.example.price_comparator.entity.User;
import com.example.price_comparator.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;

    public AuthController(UserRepository users) {
        this.users = users;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        String name = body.getOrDefault("name","").trim();
        String email = body.getOrDefault("email","").trim().toLowerCase();

        if (name.isBlank()) return ResponseEntity.badRequest().body(Map.of("error","Name is required"));
        if (email.isBlank() || !email.contains("@")) return ResponseEntity.badRequest().body(Map.of("error","Valid email is required"));

        User user = users.findByEmailIgnoreCase(email).orElseGet(() -> {
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            return users.save(u);
        });

        return ResponseEntity.ok(Map.of("userId", user.getId(), "name", user.getName(), "email", user.getEmail()));
    }
}
