package com.example.price_comparator.controller;

import com.example.price_comparator.entity.*;
import com.example.price_comparator.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracked")
public class TrackedProductController {

    private final UserRepository users;
    private final ProductRepository products;
    private final TrackedProductRepository tracked;

    public TrackedProductController(UserRepository users,
                                    ProductRepository products,
                                    TrackedProductRepository tracked) {
        this.users = users;
        this.products = products;
        this.tracked = tracked;
    }

    @PostMapping
    public ResponseEntity<?> track(@RequestBody Map<String,Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String url = body.get("url").toString();
        String title = body.get("title").toString();
        String site = body.get("site").toString();
        Double price = body.get("price") == null ? null :
                Double.valueOf(body.get("price").toString());

        User user = users.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = products.findByUrl(url)
                .orElseGet(() -> {
                    Product p = new Product();
                    p.setUrl(url);
                    p.setTitle(title);
                    p.setSite(site);
                    return products.save(p);
                });

        TrackedProduct tp = new TrackedProduct();
        tp.setUser(user);
        tp.setProduct(product);
        tp.setLastSeenPrice(price);
        tracked.save(tp);

        return ResponseEntity.ok(Map.of(
                "trackedId", tp.getId(),
                "product", product.getTitle()
        ));
    }
}