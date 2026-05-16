package com.medibook.auth.service.impl;

import com.medibook.auth.config.JwtUtil;
import com.medibook.auth.entity.User;
import com.medibook.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private RestTemplate restTemplate;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("Test@123");
        user.setRole("PATIENT");
        user.setActive(true);
    }

    // ── register ──

    @Test
    void register_Success() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.register(user);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> authService.register(user));

        assertEquals("Email already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ── login ──

    @Test
    void login_Success() {
        user.setPasswordHash("encodedPassword");
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Test@123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", "PATIENT")).thenReturn("jwt-token");

        String token = authService.login("test@example.com", "Test@123");

        assertNotNull(token);
        assertEquals("jwt-token", token);
        verify(jwtUtil, times(1)).generateToken(anyString(), anyString());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("notfound@example.com"))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> authService.login("notfound@example.com", "Test@123"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        user.setPasswordHash("encodedPassword");
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> authService.login("test@example.com", "wrongPassword"));

        assertEquals("Invalid password", ex.getMessage());
    }

    // ── getUserByEmail ──

    @Test
    void getUserByEmail_Found() {
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(user));

        User result = authService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_NotFound_ThrowsException() {
        when(userRepository.findByEmail("notfound@example.com"))
            .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> authService.getUserByEmail("notfound@example.com"));
    }

    // ── getUserById ──

    @Test
    void getUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = authService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> authService.getUserById(99L));
    }

    // ── deactivateAccount ──

    @Test
    void deactivateAccount_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.deactivateAccount(1L);

        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ── changePassword ──

    @Test
    void changePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewPass@123")).thenReturn("newEncodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.changePassword(1L, "NewPass@123");

        verify(passwordEncoder, times(1)).encode("NewPass@123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ── updateProfile ──

    @Test
    void updateProfile_Success() {
        User updatedUser = new User();
        updatedUser.setFullName("Updated Name");
        updatedUser.setPhone("9876543210");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.updateProfile(1L, updatedUser);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }
}