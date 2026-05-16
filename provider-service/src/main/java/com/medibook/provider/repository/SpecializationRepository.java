package com.medibook.provider.repository;

import com.medibook.provider.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    List<Specialization> findByIsActiveTrue();
    boolean existsByName(String name);
}