package com.example.price_comparator.repository;

import com.example.price_comparator.entity.TrackedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link com.example.price_comparator.entity.TrackedProduct}
 * entities.
 *
 * <p>Provides CRUD operations for user-product tracking records.</p>
 */
public interface TrackedProductRepository extends JpaRepository<TrackedProduct, Long> {
}