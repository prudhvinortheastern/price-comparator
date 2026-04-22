package com.example.price_comparator.controller;

import com.example.price_comparator.entity.*;
import com.example.price_comparator.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for product tracking operations.
 *
 * <p>Creates a user-product tracking record and stores the last seen price.</p>
 */
@RestController
@RequestMapping("/api/tracked")
public class TrackedProductController {

    private final UserRepository users;
    private final ProductRepository products;
    private final TrackedProductRepository tracked;

    /**
     * Creates a {@code TrackedProductController} with repository dependencies.
     *
     * @param users repository for user lookups
     * @param products repository for product persistence and lookup
     * @param tracked repository for tracked product records
     */
    public TrackedProductController(UserRepository users,
                                    ProductRepository products,
                                    TrackedProductRepository tracked) {
        this.users = users;
        this.products = products;
        this.tracked = tracked;
    }

    /**
     * Tracks a product for a given user.
     *
     * <p>Expected body keys: {@code userId}, {@code url}, {@code title},
     * {@code site}, and optional {@code price}.</p>
     *
     * @param body request payload containing tracking details
     * @return HTTP 200 response with tracked record id and product title
     */
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