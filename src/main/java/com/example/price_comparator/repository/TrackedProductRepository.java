package com.example.price_comparator.repository;

import com.example.price_comparator.entity.TrackedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackedProductRepository extends JpaRepository<TrackedProduct, Long> {
}