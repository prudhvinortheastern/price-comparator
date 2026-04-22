package com.example.price_comparator.controller;

import com.example.price_comparator.entity.User;
import com.example.price_comparator.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST controller for authentication-related endpoints.
 *
 * <p>Currently exposes user registration for the browser extension.</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Creates an {@code AuthController} with the required authentication service.
     *
     * @param authService service used for user registration logic
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user from the provided request payload.
     *
     * <p>Expected body keys: {@code name}, {@code email}.</p>
     *
     * @param body request payload containing user registration fields
     * @return HTTP 200 with user data when registration succeeds, or HTTP 400
     *         with an error message when validation fails
     */
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