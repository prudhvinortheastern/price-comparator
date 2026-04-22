package com.example.price_comparator.repository;

import com.example.price_comparator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link com.example.price_comparator.entity.User} entities.
 *
 * <p>Provides standard CRUD operations via {@code JpaRepository} and
 * custom lookup methods used by authentication logic.</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by email using a case-insensitive comparison.
     *
     * @param email user email address
     * @return optional containing the matched user when found
     */
    Optional<User> findByEmailIgnoreCase(String email);
}
