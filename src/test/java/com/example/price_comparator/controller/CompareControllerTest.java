package com.example.price_comparator.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-layer slice tests for {@link SimilarProductsController}.
 *
 * <p>These tests verify that /api/compare returns similar-product links
 * for supported shopping sites and follows the expected JSON contract.</p>
 */
@WebMvcTest(SimilarProductsController.class)
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

    @Test
    @DisplayName("POST /api/compare returns 200 OK")
    void compare_validRequest_returns200() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/compare response contains hasSimilar field")
    void compare_responseContainsHasSimilar() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.hasSimilar").value(true));
    }

    @Test
    @DisplayName("POST /api/compare echoes currentSite from request")
    void compare_responseContainsCurrentSite() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.currentSite").value("amazon"));
    }

    @Test
    @DisplayName("POST /api/compare returns three offers")
    void compare_responseContainsThreeOffers() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.offers").isArray())
                .andExpect(jsonPath("$.offers.length()").value(3));
    }

    @Test
    @DisplayName("POST /api/compare offers contain site and url fields")
    void compare_offersHaveRequiredFields() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.offers[0].site").exists())
                .andExpect(jsonPath("$.offers[0].url").exists())
                .andExpect(jsonPath("$.offers[1].site").exists())
                .andExpect(jsonPath("$.offers[1].url").exists())
                .andExpect(jsonPath("$.offers[2].site").exists())
                .andExpect(jsonPath("$.offers[2].url").exists());
    }

    @Test
    @DisplayName("POST /api/compare includes expected site names")
    void compare_offersContainExpectedSites() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.offers[0].site").value("eBay"))
                .andExpect(jsonPath("$.offers[1].site").value("Walmart"))
                .andExpect(jsonPath("$.offers[2].site").value("New egg"));
    }

    @Test
    @DisplayName("POST /api/compare offer URLs encode the product title")
    void compare_offerUrlsEncodeTitle() throws Exception {
        mockMvc.perform(post("/api/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(jsonPath("$.offers[0].url",
                        Matchers.containsString("Wireless+Headphones")))
                .andExpect(jsonPath("$.offers[1].url",
                        Matchers.containsString("Wireless+Headphones")))
                .andExpect(jsonPath("$.offers[2].url",
                        Matchers.containsString("Wireless+Headphones")));
    }

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
                .andExpect(jsonPath("$.hasSimilar").value(true))
                .andExpect(jsonPath("$.offers").isArray());
    }
}