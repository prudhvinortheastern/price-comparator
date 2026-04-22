package com.example.price_comparator.repository;

import com.example.price_comparator.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link com.example.price_comparator.entity.Product} entities.
 *
 * <p>Provides standard CRUD operations and URL-based product lookup.</p>
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Finds a product by its canonical URL.
     *
     * @param url product URL
     * @return optional containing the matched product when found
     */
    Optional<Product> findByUrl(String url);
}