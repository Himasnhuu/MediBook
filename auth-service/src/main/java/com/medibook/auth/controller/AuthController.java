package com.medibook.auth.controller;

import com.medibook.auth.dto.LoginRequest;
import com.medibook.auth.entity.User;
import com.medibook.auth.repository.UserRepository;
import com.medibook.auth.service.AuthService;
import com.medibook.auth.service.EmailService;
import com.medibook.auth.service.OtpStore;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OtpStore otpStore;

	@Autowired
	private EmailService emailService;

	// ✅ Register
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user) {
		return ResponseEntity.ok(authService.register(user));
	}

	// ✅ Login
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest request) {
		String token = authService.login(request.getEmail(), request.getPassword());
		return ResponseEntity.ok(token);
	}

	// ✅ Get user by email
	@GetMapping("/user")
	public ResponseEntity<User> getUser(@RequestParam String email) {
		return ResponseEntity.ok(authService.getUserByEmail(email));
	}

	// ✅ Update profile
	@PutMapping("/profile/{id}")
	public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User user) {
		return ResponseEntity.ok(authService.updateProfile(id, user));
	}

	// ✅ Change password
	@PutMapping("/password/{id}")
	public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestParam String password) {
		authService.changePassword(id, password);
		return ResponseEntity.ok("Password updated");
	}

	// ✅ Deactivate account
	@DeleteMapping("/deactivate/{id}")
	public ResponseEntity<String> deactivate(@PathVariable Long id) {
		authService.deactivateAccount(id);
		return ResponseEntity.ok("Account deactivated");
	}

	@GetMapping("/users/all")
	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.ok(userRepository.findAll());
	}

	// Send OTP before registration
	@PostMapping("/send-otp")
	public ResponseEntity<String> sendOtp(@RequestParam String email) {
		// Check if email already exists
		if (userRepository.existsByEmail(email)) {
			return ResponseEntity.badRequest().body("Email already registered");
		}
		String otp = otpStore.generateOtp(email);
		emailService.sendOtpEmail(email, otp);
		return ResponseEntity.ok("OTP sent to " + email);
	}

	// Verify OTP
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
		if (otpStore.validateOtp(email, otp)) {
			otpStore.clearOtp(email);
			return ResponseEntity.ok("OTP verified");
		}
		return ResponseEntity.badRequest().body("Invalid or expired OTP");
	}

	// Forgot password — Step 1: send OTP
	@PostMapping("/forgot-password/send-otp")
	public ResponseEntity<String> forgotPasswordSendOtp(@RequestParam String email) {
		if (!userRepository.existsByEmail(email)) {
			return ResponseEntity.badRequest().body("No account found with this email");
		}
		String otp = otpStore.generateOtp(email);
		emailService.sendOtpEmail(email, otp);
		return ResponseEntity.ok("OTP sent to " + email);
	}

	// Forgot password — Step 2: verify OTP + reset password
	@PostMapping("/forgot-password/reset")
	public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String otp,
			@RequestParam String newPassword) {
		if (!otpStore.validateOtp(email, otp)) {
			return ResponseEntity.badRequest().body("Invalid or expired OTP");
		}
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		user.setPasswordHash(new BCryptPasswordEncoder().encode(newPassword));
		userRepository.save(user);
		otpStore.clearOtp(email);
		return ResponseEntity.ok("Password reset successfully");
	}

	// Change password — for logged in users
	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(@RequestParam String email, @RequestParam String currentPassword,
			@RequestParam String newPassword) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(currentPassword, user.getPasswordHash())) {
			return ResponseEntity.badRequest().body("Current password is incorrect");
		}
		user.setPasswordHash(encoder.encode(newPassword));
		userRepository.save(user);
		return ResponseEntity.ok("Password changed successfully");
	}
}