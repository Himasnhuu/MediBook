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
		existing.setIsVerified(false);

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

	@Override
	public Provider submitProfileUpdate(Long id, Provider updated) {
		Provider existing = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));

		// Save changes to PENDING fields only — don't touch live fields
		existing.setPendingSpecialization(updated.getSpecialization());
		existing.setPendingQualification(updated.getQualification());
		existing.setPendingExperienceYears(updated.getExperienceYears());
		existing.setPendingBio(updated.getBio());
		existing.setPendingClinicName(updated.getClinicName());
		existing.setPendingClinicAddress(updated.getClinicAddress());
		existing.setPendingConsultationFee(updated.getConsultationFee());
		existing.setHasPendingChanges(true);

		return providerRepository.save(existing);
	}

	@Override
	public Provider approveProfileUpdate(Long id) {
		Provider existing = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));

		// Copy pending fields to live fields
		if (existing.getPendingSpecialization() != null)
			existing.setSpecialization(existing.getPendingSpecialization());
		if (existing.getPendingQualification() != null)
			existing.setQualification(existing.getPendingQualification());
		if (existing.getPendingExperienceYears() != null)
			existing.setExperienceYears(existing.getPendingExperienceYears());
		if (existing.getPendingBio() != null)
			existing.setBio(existing.getPendingBio());
		if (existing.getPendingClinicName() != null)
			existing.setClinicName(existing.getPendingClinicName());
		if (existing.getPendingClinicAddress() != null)
			existing.setClinicAddress(existing.getPendingClinicAddress());
		if (existing.getPendingConsultationFee() != null)
			existing.setConsultationFee(existing.getPendingConsultationFee());

		// Clear pending fields
		existing.setPendingSpecialization(null);
		existing.setPendingQualification(null);
		existing.setPendingExperienceYears(null);
		existing.setPendingBio(null);
		existing.setPendingClinicName(null);
		existing.setPendingClinicAddress(null);
		existing.setPendingConsultationFee(null);
		existing.setHasPendingChanges(false);

		return providerRepository.save(existing);
	}

	@Override
	public Provider rejectProfileUpdate(Long id) {
		Provider existing = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));

		// Just clear pending fields — keep live fields as is
		existing.setPendingSpecialization(null);
		existing.setPendingQualification(null);
		existing.setPendingExperienceYears(null);
		existing.setPendingBio(null);
		existing.setPendingClinicName(null);
		existing.setPendingClinicAddress(null);
		existing.setPendingConsultationFee(null);
		existing.setHasPendingChanges(false);

		return providerRepository.save(existing);
	}

	@Override
	public Provider updateProfilePhoto(Long id, String photoUrl) {
		Provider provider = providerRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Provider not found"));
		provider.setProfilePhotoUrl(photoUrl);
		return providerRepository.save(provider);
	}

}
