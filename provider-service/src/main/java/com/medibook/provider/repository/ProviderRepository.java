package com.medibook.provider.repository;

import com.medibook.provider.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByUserId(Long userId);

    List<Provider> findBySpecialization(String spec);

    List<Provider> findByIsVerified(Boolean isVerified);

    List<Provider> findByIsAvailable(Boolean isAvailable);
    
    List<Provider> findByHasPendingChangesTrue();

    @Query("SELECT p FROM Provider p WHERE LOWER(p.clinicName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.specialization) LIKE LOWER(CONCAT('%', :spec, '%'))")
    List<Provider> searchByNameOrSpecialization(@Param("name") String name, @Param("spec") String spec);

    List<Provider> findByClinicAddress(String address);

    long countBySpecialization(String spec);
}
