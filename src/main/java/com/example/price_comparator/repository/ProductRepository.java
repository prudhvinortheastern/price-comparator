package com.example.price_comparator.repository;

import com.example.price_comparator.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByUrl(String url);
}