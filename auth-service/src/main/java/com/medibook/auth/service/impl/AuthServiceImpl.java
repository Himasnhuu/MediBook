package com.medibook.auth.service.impl;

import com.medibook.auth.config.JwtUtil;
import com.medibook.auth.entity.User;
import com.medibook.auth.repository.UserRepository;
import com.medibook.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public User register(User user) {

		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		user.setCreatedAt(LocalDateTime.now());
		user.setActive(true);

		// Encrypt password
		user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

		// ✅ Step 1: Save user FIRST
		User savedUser = userRepository.save(user);

		// ✅ Step 2: Call notification service
		try {
			String notifUrl = "http://localhost:8088/notifications";

			Map<String, String> notif = new HashMap<>();
			notif.put("recipientId", String.valueOf(savedUser.getId()));
			notif.put("type", "BOOKING"); // you can change to WELCOME
			notif.put("title", "Welcome to MediBook!");
			notif.put("message", "Your account has been created successfully.");
			notif.put("channel", "APP");

			restTemplate.postForObject(notifUrl, notif, Object.class);

		} catch (Exception e) {
			// Don't fail registration if notification fails
			System.out.println("Notification failed: " + e.getMessage());
		}

		// ✅ Step 3: Return user
		return savedUser;
	}

	@Override
	public String login(String email, String password) {

		Optional<User> optionalUser = userRepository.findByEmail(email);

		if (optionalUser.isEmpty()) {
			throw new RuntimeException("User not found");
		}

		User user = optionalUser.get();

		if (!passwordEncoder.matches(password, user.getPasswordHash())) {
			throw new RuntimeException("Invalid password");
		}

		return jwtUtil.generateToken(user.getEmail(), user.getRole());
	}

	@Override
	public void logout(String token) {
		// JWT logout logic later
	}

	@Override
	public boolean validateToken(String token) {
		return jwtUtil.validateToken(token);
	}

	@Override
	public String refreshToken(String token) {
		return token; // temporary
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public User updateProfile(Long userId, User updatedUser) {

		User user = getUserById(userId);

		user.setFullName(updatedUser.getFullName());
		user.setPhone(updatedUser.getPhone());
		user.setProfilePicUrl(updatedUser.getProfilePicUrl());

		return userRepository.save(user);
	}

	@Override
	public void changePassword(Long userId, String newPassword) {

		User user = getUserById(userId);
		user.setPasswordHash(passwordEncoder.encode(newPassword));

		userRepository.save(user);
	}

	@Override
	public void deactivateAccount(Long userId) {

		User user = getUserById(userId);
		user.setActive(false);

		userRepository.save(user);
	}
}