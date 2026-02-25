package com.example.price_comparator.controller;

import com.example.price_comparator.entity.User;
import com.example.price_comparator.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User user = authService.register(body.get("name"), body.get("email"));
            return ResponseEntity.ok(Map.of(
                    "userId", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}