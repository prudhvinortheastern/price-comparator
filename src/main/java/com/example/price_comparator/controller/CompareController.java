package com.example.price_comparator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompareController {

    @PostMapping("/compare")
    public ResponseEntity<?> compare(@RequestBody Map<String, Object> body) {
        // For MVP: return a mocked cheaper offer to prove the pipeline works
        // Later we replace this with real API calls (eBay, Walmart API, etc.)
        Map<String, Object> result = Map.of(
                "cheaperFound", true,
                "currentSite", body.getOrDefault("site", "unknown"),
                "currentPrice", body.getOrDefault("price", null),
                "offers", List.of(
                        Map.of("site", "eBay", "price", 149.99, "url", "https://www.ebay.com/sch/i.html?_nkw=" + urlEncode(String.valueOf(body.getOrDefault("title","")))),
                        Map.of("site", "Walmart", "price", 155.49, "url", "https://www.walmart.com/search?q=" + urlEncode(String.valueOf(body.getOrDefault("title",""))))
                )
        );
        return ResponseEntity.ok(result);
    }

    private String urlEncode(String s) {
        return s == null ? "" : s.replace(" ", "+");
    }
}
