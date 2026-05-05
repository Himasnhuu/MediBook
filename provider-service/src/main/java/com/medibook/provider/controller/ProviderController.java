package com.medibook.provider.controller;

import com.medibook.provider.entity.Provider;
import com.medibook.provider.repository.*;
import com.medibook.provider.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/providers")
public class ProviderController {

	private final ProviderService providerService;

	public ProviderController(ProviderService providerService) {
		this.providerService = providerService;
	}

	@PostMapping
	@PreAuthorize("hasRole('PROVIDER')")
	public ResponseEntity<?> registerProvider(@RequestBody Provider provider) {
		try {
			return ResponseEntity.ok(providerService.registerProvider(provider));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<List<Provider>> getAllProviders() {
		return ResponseEntity.ok(providerService.getAllProviders());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Provider> getProviderById(@PathVariable Long id) {
		return providerService.getProviderById(id).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<Provider> getProviderByUserId(@PathVariable Long userId) {
		return ResponseEntity.ok(providerService.getProviderByUserId(userId));
	}

	@GetMapping("/specialization/{spec}")
	public ResponseEntity<List<Provider>> getBySpecialization(@PathVariable String spec) {
		return ResponseEntity.ok(providerService.getBySpecialization(spec));
	}

	@GetMapping("/search")
	public ResponseEntity<List<Provider>> searchProviders(@RequestParam String query) {
		return ResponseEntity.ok(providerService.searchProviders(query));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
	public ResponseEntity<Provider> updateProvider(@PathVariable Long id, @RequestBody Provider provider) {
		return ResponseEntity.ok(providerService.updateProvider(id, provider));
	}

	@PutMapping("/{id}/verify")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> verifyProvider(@PathVariable Long id) {
		providerService.verifyProvider(id);
		return ResponseEntity.ok("Provider verified");
	}

	@PutMapping("/{id}/availability")
	@PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
	public ResponseEntity<String> setAvailability(@PathVariable Long id, @RequestParam boolean available) {
		providerService.setAvailability(id, available);
		return ResponseEntity.ok("Availability updated");
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteProvider(@PathVariable Long id) {
		providerService.deleteProvider(id);
		return ResponseEntity.ok("Provider deleted");
	}

	@PutMapping("/{id}/submit-update")
	@PreAuthorize("hasRole('PROVIDER')")
	public ResponseEntity<Provider> submitUpdate(@PathVariable Long id, @RequestBody Provider provider) {
		return ResponseEntity.ok(providerService.submitProfileUpdate(id, provider));
	}

	@PutMapping("/{id}/approve-update")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Provider> approveUpdate(@PathVariable Long id) {
		return ResponseEntity.ok(providerService.approveProfileUpdate(id));
	}

	@PutMapping("/{id}/reject-update")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Provider> rejectUpdate(@PathVariable Long id) {
		return ResponseEntity.ok(providerService.rejectProfileUpdate(id));
	}

	@PutMapping("/{id}/photo")
	public ResponseEntity<Provider> updatePhoto(@PathVariable Long id, @RequestBody Map<String, String> body) {
		return ResponseEntity.ok(providerService.updateProfilePhoto(id, body.get("profilePhotoUrl")));
	}
}