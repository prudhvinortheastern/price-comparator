package com.example.price_comparator.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit / slice tests for {@link HomeController}.
 *
 * <p>Uses {@code @WebMvcTest} to spin up only the Spring MVC layer (no full
 * application context, no database). Verifies that the health-check endpoint
 * responds correctly so deployment platforms can trust it.</p>
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifies that {@code GET /} returns HTTP 200.
     */
    @Test
    @DisplayName("GET / returns 200 OK")
    void home_returns200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    /**
     * Verifies that {@code GET /} returns the expected status message string.
     */
    @Test
    @DisplayName("GET / returns status message in body")
    void home_returnsStatusMessage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Price Comparator Running Successfully!"));
    }
}
