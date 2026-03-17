package com.example.price_comparator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SimilarProductsController {
    private String urlEncode(String s) {
        return s == null ? "" : s.replace(" ", "+");
    }

    @PostMapping("/compare")
    public ResponseEntity<?> compare(@RequestBody Map<String, Object> body) {
        // 1) Read basic info from the request
        String site = String.valueOf(body.getOrDefault("site", "unknown"));
        String rawTitle = String.valueOf(body.getOrDefault("title", ""));
    
        // 2) Refine/clean the title a bit for better search
        String title = rawTitle
                .replace("Amazon.com Shopping Cart", "")
                .replace("Amazon.com:", "")
                .replace(" - Amazon.com", "")
                .replace("Amazon.com", "")
                .trim();
    
        if (title.isEmpty()) {
            title = rawTitle.trim();
        }
        if (title.isEmpty()) {
            title = "product";
        }
    
        String encodedTitle = urlEncode(title);
    
        // 3) Build search URLs on other sites using that title
        List<Map<String, Object>> offers = List.of(
                Map.of(
                        "site", "eBay",
                        "url", "https://www.ebay.com/sch/i.html?_nkw=" + encodedTitle
                ),
                Map.of(
                        "site", "Walmart",
                        "url", "https://www.walmart.com/search?q=" + encodedTitle
                ),
                Map.of(
                        "site", "New egg",
                        "url", "https://www.newegg.com/p/pl?d=" + encodedTitle
                )
        );
    
        // 4) Return a "similar products" response (no prices, no real comparison)
        Map<String, Object> result = Map.of(
                "hasSimilar", !offers.isEmpty(),
                "currentSite", site,
                "offers", offers
        );
    
        return ResponseEntity.ok(result);
    }
}
