package com.medibook.auth.controller;

import com.medibook.auth.dto.LoginRequest;
import com.medibook.auth.entity.User;
import com.medibook.auth.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ✅ Register
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {
        String token = authService.login(
            request.getEmail(),
            request.getPassword()
        );
        return ResponseEntity.ok(token);
    }

    // ✅ Get user by email
    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestParam String email) {
        return ResponseEntity.ok(authService.getUserByEmail(email));
    }

    // ✅ Update profile
    @PutMapping("/profile/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id,
                                              @RequestBody User user) {
        return ResponseEntity.ok(authService.updateProfile(id, user));
    }

    // ✅ Change password
    @PutMapping("/password/{id}")
    public ResponseEntity<String> changePassword(@PathVariable Long id,
                                                @RequestParam String password) {
        authService.changePassword(id, password);
        return ResponseEntity.ok("Password updated");
    }

    // ✅ Deactivate account
    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivate(@PathVariable Long id) {
        authService.deactivateAccount(id);
        return ResponseEntity.ok("Account deactivated");
    }
}