package com.medibook.provider.controller;

import com.medibook.provider.entity.Specialization;
import com.medibook.provider.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/specializations")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationRepository specializationRepository;

    // Public — anyone can view
    @GetMapping
    public ResponseEntity<List<Specialization>> getAll() {
        return ResponseEntity.ok(
            specializationRepository.findByIsActiveTrue());
    }

    // Admin only — add
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> add(@RequestBody Specialization specialization) {
        if (specializationRepository.existsByName(specialization.getName())) {
            return ResponseEntity.badRequest()
                .body("Specialization already exists");
        }
        specialization.setIsActive(true);
        return ResponseEntity.ok(
            specializationRepository.save(specialization));
    }

    // Admin only — delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        specializationRepository.deleteById(id);
        return ResponseEntity.ok("Specialization deleted");
    }
}