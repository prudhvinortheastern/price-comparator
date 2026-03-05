package com.example.price_comparator.controller;

import com.example.price_comparator.entity.User;
import com.example.price_comparator.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-layer slice tests for {@link AuthController}.
 *
 * <p>{@link AuthService} is replaced with a Mockito mock so these tests focus
 * purely on HTTP serialisation, routing, and status-code logic without touching
 * the database or the real service implementation.</p>
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    /**
     * Verifies that a valid registration request returns HTTP 200 and the
     * correct JSON fields ({@code userId}, {@code name}, {@code email}).
     */
    @Test
    @DisplayName("POST /api/auth/register with valid body returns 200 and user JSON")
    void register_validRequest_returns200WithUser() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Alice");
        mockUser.setEmail("alice@example.com");

        when(authService.register("Alice", "alice@example.com")).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alice\",\"email\":\"alice@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    /**
     * Verifies that when {@link AuthService} throws an {@link IllegalArgumentException}
     * (e.g., blank name), the controller returns HTTP 400 with an {@code error} field.
     */
    @Test
    @DisplayName("POST /api/auth/register with invalid input returns 400 with error message")
    void register_invalidInput_returns400WithError() throws Exception {
        when(authService.register("", "bad"))
                .thenThrow(new IllegalArgumentException("Name is required"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"bad\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Name is required"));
    }

    /**
     * Verifies that the controller correctly surfaces the "Valid email is required"
     * error message when the service rejects a missing {@code @} in the e-mail.
     */
    @Test
    @DisplayName("POST /api/auth/register with missing @ in email returns 400")
    void register_invalidEmail_returns400() throws Exception {
        when(authService.register("Alice", "notanemail"))
                .thenThrow(new IllegalArgumentException("Valid email is required"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alice\",\"email\":\"notanemail\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Valid email is required"));
    }

    /**
     * Verifies that the same endpoint is reachable for a second registration with
     * the same e-mail (idempotency at the HTTP layer — the service mock returns
     * the existing user).
     */
    @Test
    @DisplayName("POST /api/auth/register with duplicate email still returns 200")
    void register_duplicateEmail_returns200() throws Exception {
        User existing = new User();
        existing.setId(5L);
        existing.setName("Alice");
        existing.setEmail("alice@example.com");

        when(authService.register("Alice", "alice@example.com")).thenReturn(existing);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alice\",\"email\":\"alice@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(5));
    }
}
