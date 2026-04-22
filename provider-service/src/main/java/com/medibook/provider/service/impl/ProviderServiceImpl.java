package com.medibook.provider.service.impl;

import com.medibook.provider.entity.Provider;
import com.medibook.provider.repository.ProviderRepository;
import com.medibook.provider.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

	private final ProviderRepository providerRepository;

	@Override
	public Provider registerProvider(Provider provider) {

		// Check if provider already exists for this userId
		if (providerRepository.findByUserId(provider.getUserId()).isPresent()) {
			throw new RuntimeException("Provider already registered for userId: " + provider.getUserId());
		}
		provider.setCreatedAt(LocalDate.now());
		provider.setIsVerified(false);
		provider.setIsAvailable(true);
		provider.setAvgRating(0.0);
		return providerRepository.save(provider);
	}

	@Override
	public Optional<Provider> getProviderById(Long id) {
		return providerRepository.findById(id);
	}

	@Override
	public Provider getProviderByUserId(Long userId) {
		return providerRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Provider not found"));
	}

	@Override
	public List<Provider> getBySpecialization(String spec) {
		return providerRepository.findBySpecialization(spec);
	}

	@Override
	public List<Provider> searchProviders(String query) {
		return providerRepository.searchByNameOrSpecialization(query, query);
	}

	@Override
	public Provider updateProvider(Long id, Provider provider) {
		Provider existing = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));

		existing.setUserId(provider.getUserId());
		existing.setSpecialization(provider.getSpecialization());
		existing.setQualification(provider.getQualification());
		existing.setExperienceYears(provider.getExperienceYears());
		existing.setBio(provider.getBio());
		existing.setClinicName(provider.getClinicName());
		existing.setClinicAddress(provider.getClinicAddress());
		existing.setAvgRating(provider.getAvgRating());
		existing.setIsVerified(provider.getIsVerified());
		existing.setIsAvailable(provider.getIsAvailable());

		return providerRepository.save(existing);
	}

	@Override
	public void verifyProvider(Long id) {
		Provider provider = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));
		provider.setIsVerified(true);
		providerRepository.save(provider);
	}

	@Override
	public void setAvailability(Long id, boolean available) {
		Provider provider = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));
		provider.setIsAvailable(available);
		providerRepository.save(provider);
	}

	@Override
	public void deleteProvider(Long id) {
		Provider provider = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));
		providerRepository.delete(provider);
	}

	@Override
	public void updateRating(Long id, double rating) {
		Provider provider = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));
		provider.setAvgRating(rating);
		providerRepository.save(provider);
	}

	@Override
	public List<Provider> getAllProviders() {
		return providerRepository.findAll();
	}
}
