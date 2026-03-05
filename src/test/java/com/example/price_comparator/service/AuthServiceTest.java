package com.example.price_comparator.service;

import com.example.price_comparator.entity.User;
import com.example.price_comparator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthService}.
 *
 * <p>Uses Mockito to replace the real {@link UserRepository} with a test double,
 * so no database or Spring application context is needed. Each test exercises a
 * single, isolated branch of the registration logic.</p>
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    private AuthService authService;

    /**
     * Creates a fresh {@link AuthService} before each test, injecting the Mockito mock.
     */
    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository);
    }

    // -----------------------------------------------------------------------
    // Happy-path tests
    // -----------------------------------------------------------------------

    /**
     * Verifies that a brand-new user is persisted and returned when the e-mail
     * does not yet exist in the repository.
     */
    @Test
    @DisplayName("register: new user is saved and returned")
    void register_newUser_savesAndReturns() {
        when(userRepository.findByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.empty());

        User saved = new User();
        saved.setName("Alice");
        saved.setEmail("alice@example.com");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = authService.register("Alice", "alice@example.com");

        assertThat(result.getName()).isEqualTo("Alice");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Verifies that when the same e-mail is registered a second time, the existing
     * user is returned and no new record is saved (idempotency guarantee).
     */
    @Test
    @DisplayName("register: duplicate email returns existing user without saving again")
    void register_duplicateEmail_returnsExistingUser() {
        User existing = new User();
        existing.setName("Alice");
        existing.setEmail("alice@example.com");

        when(userRepository.findByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.of(existing));

        User result = authService.register("Alice Again", "alice@example.com");

        assertThat(result).isSameAs(existing);
        verify(userRepository, never()).save(any());
    }

    /**
     * Verifies that e-mail lookup is truly case-insensitive: registering with
     * {@code "ALICE@EXAMPLE.COM"} finds the existing {@code "alice@example.com"} account.
     */
    @Test
    @DisplayName("register: email matching is case-insensitive")
    void register_emailCaseInsensitive_returnsExistingUser() {
        User existing = new User();
        existing.setEmail("alice@example.com");

        when(userRepository.findByEmailIgnoreCase("alice@example.com"))
                .thenReturn(Optional.of(existing));

        User result = authService.register("Alice", "ALICE@EXAMPLE.COM");

        assertThat(result).isSameAs(existing);
        verify(userRepository, never()).save(any());
    }

    /**
     * Verifies that leading and trailing whitespace in the name is stripped
     * before the user is persisted.
     */
    @Test
    @DisplayName("register: name whitespace is trimmed before saving")
    void register_nameIsTrimmed() {
        when(userRepository.findByEmailIgnoreCase("bob@example.com"))
                .thenReturn(Optional.empty());

        User saved = new User();
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register("  Bob  ", "bob@example.com");

        assertThat(result.getName()).isEqualTo("Bob");
    }

    // -----------------------------------------------------------------------
    // Validation-failure tests
    // -----------------------------------------------------------------------

    /**
     * Verifies that a blank name throws {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("register: blank name throws IllegalArgumentException")
    void register_blankName_throwsException() {
        assertThatThrownBy(() -> authService.register("  ", "alice@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name is required");
    }

    /**
     * Verifies that a {@code null} name throws {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("register: null name throws IllegalArgumentException")
    void register_nullName_throwsException() {
        assertThatThrownBy(() -> authService.register(null, "alice@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name is required");
    }

    /**
     * Verifies that an e-mail without an {@code @} character is rejected.
     */
    @Test
    @DisplayName("register: email without @ throws IllegalArgumentException")
    void register_emailMissingAtSign_throwsException() {
        assertThatThrownBy(() -> authService.register("Alice", "notanemail"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valid email is required");
    }

    /**
     * Verifies that a blank e-mail throws {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("register: blank email throws IllegalArgumentException")
    void register_blankEmail_throwsException() {
        assertThatThrownBy(() -> authService.register("Alice", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valid email is required");
    }

    /**
     * Verifies that a {@code null} e-mail throws {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("register: null email throws IllegalArgumentException")
    void register_nullEmail_throwsException() {
        assertThatThrownBy(() -> authService.register("Alice", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valid email is required");
    }
}
