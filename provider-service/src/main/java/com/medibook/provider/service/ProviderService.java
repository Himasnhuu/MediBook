package com.medibook.provider.service;

import com.medibook.provider.entity.Provider;

import java.util.List;
import java.util.Optional;

public interface ProviderService {

    Provider registerProvider(Provider provider);

    Optional<Provider> getProviderById(Long id);

    Provider getProviderByUserId(Long userId);

    List<Provider> getBySpecialization(String spec);

    List<Provider> searchProviders(String query);

    Provider updateProvider(Long id, Provider provider);

    void verifyProvider(Long id);

    void setAvailability(Long id, boolean available);

    void deleteProvider(Long id);

    void updateRating(Long id, double rating);

    List<Provider> getAllProviders();
}
