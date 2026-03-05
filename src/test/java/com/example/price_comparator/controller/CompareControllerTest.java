package com.example.price_comparator.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-layer slice tests for {@link CompareController}.
 *
 * <p>Because the prototype controller returns hardcoded mock data (no external
 * dependencies), these tests validate the full response structure and confirm
 * that the endpoint behaves correctly for the extension's consumer contract.
 * They will remain valid after the final version substitutes live API calls,
 * provided the response schema stays the same.</p>
 */
@WebMvcTest(CompareController.class)
class CompareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String VALID_BODY = """
            {
              "userId": 1,
              "site":   "amazon",
              "url":    "https://www.amazon.com/dp/B08XYZ",
              "title":  "Wireless Headphones",
              "price":  159.99
            }
            """;

    /**
     * Verifies that a valid comparison request returns HTTP 200.
     */
    @Test
    @DisplayName("POST /api/compare returns 200 OK")
    void compare_validRequest_returns200() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk());
    }

    /**
     * Verifies that the response always contains {@code cheaperFound: true}
     * in the current prototype.
     */
    @Test
    @DisplayName("POST /api/compare response contains cheaperFound field")
    void compare_responseCheaperFoundTrue() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.cheaperFound").value(true));
    }

    /**
     * Verifies that the response echoes the {@code currentSite} field from the
     * request body.
     */
    @Test
    @DisplayName("POST /api/compare echoes currentSite from request")
    void compare_responseContainsCurrentSite() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.currentSite").value("amazon"));
    }

    /**
     * Verifies that the response includes a non-empty {@code offers} array with
     * at least two competitor entries (eBay and Walmart in the prototype).
     */
    @Test
    @DisplayName("POST /api/compare returns at least two offers")
    void compare_responseContainsTwoOffers() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.offers").isArray())
                .andExpect(jsonPath("$.offers.length()").value(2));
    }

    /**
     * Verifies that each offer object includes the required {@code site},
     * {@code price}, and {@code url} fields.
     */
    @Test
    @DisplayName("POST /api/compare offers contain site, price, and url fields")
    void compare_offersHaveRequiredFields() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.offers[0].site").exists())
                .andExpect(jsonPath("$.offers[0].price").exists())
                .andExpect(jsonPath("$.offers[0].url").exists())
                .andExpect(jsonPath("$.offers[1].site").exists())
                .andExpect(jsonPath("$.offers[1].price").exists())
                .andExpect(jsonPath("$.offers[1].url").exists());
    }

    /**
     * Verifies that the offer URLs encode the product title as a search query,
     * replacing spaces with {@code +} so they are valid search URLs.
     */
    @Test
    @DisplayName("POST /api/compare offer URLs encode the product title")
    void compare_offerUrlsEncodeTitle() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.offers[0].url").value(
                        org.hamcrest.Matchers.containsString("Wireless+Headphones")))
                .andExpect(jsonPath("$.offers[1].url").value(
                        org.hamcrest.Matchers.containsString("Wireless+Headphones")));
    }

    /**
     * Verifies that the endpoint handles a request with a missing {@code title}
     * field gracefully (no 500 error), using a blank encoded string in the URLs.
     */
    @Test
    @DisplayName("POST /api/compare with missing title does not throw 500")
    void compare_missingTitle_returnsGracefully() throws Exception {
        String bodyNoTitle = """
                { "userId": 1, "site": "amazon", "url": "https://amazon.com/dp/X" }
                """;

        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyNoTitle))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cheaperFound").value(true));
    }
}
