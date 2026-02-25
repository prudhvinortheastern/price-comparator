package com.example.price_comparator.service;

import com.example.price_comparator.entity.User;
import com.example.price_comparator.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }

    public User register(String name, String email) {
        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null ? "" : email.trim().toLowerCase();

        if (cleanName.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }

        return users.findByEmailIgnoreCase(cleanEmail)
                .orElseGet(() -> {
                    User u = new User();
                    u.setName(cleanName);
                    u.setEmail(cleanEmail);
                    return users.save(u);
                });
    }
}

