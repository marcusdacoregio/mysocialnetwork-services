package com.dooall.mysocialnetwork.profiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
public class ProfileService {

	private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

	private final ProfileRepository profileRepository;

	public ProfileService(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	/**
	 * Updates a profile's display name and bio.
	 * Only the profile owner can update their profile.
	 *
	 * @param profileId   The ID of the profile to update
	 * @param userId      The ID of the user making the request
	 * @param displayName The new display name (optional, can be null to keep existing)
	 * @param bio         The new bio (optional, can be null to keep existing)
	 * @return The updated profile
	 * @throws IllegalArgumentException If the profile doesn't exist
	 * @throws SecurityException        If the user is not authorized to update the profile
	 */
	@Transactional
	public ProfileResponse updateProfile(UUID profileId, UUID userId, String displayName, String bio) {
		log.info("Attempting to update profile {} for user {}", profileId, userId);

		Profile profile = getProfileEntity(profileId);

		if (displayName != null) {
			profile.setDisplayName(displayName);
		}

		if (bio != null) {
			profile.setBio(bio);
		}

		Profile updatedProfile = this.profileRepository.save(profile);
		log.info("Profile {} updated successfully", profileId);
		return ProfileResponse.fromProfile(updatedProfile);
	}

	/**
	 * Updates a profile's handle.
	 * Only the profile owner can update their handle.
	 * The handle must be unique.
	 *
	 * @param profileId The ID of the profile to update
	 * @param userId    The ID of the user making the request
	 * @param handle    The new handle
	 * @return The updated profile
	 * @throws IllegalArgumentException If the profile doesn't exist or the handle is already taken
	 * @throws SecurityException        If the user is not authorized to update the profile
	 */
	@Transactional
	public ProfileResponse updateHandle(UUID profileId, UUID userId, String handle) {
		log.info("Attempting to update handle for profile {} to {}", profileId, handle);

		if (this.profileRepository.existsByHandle(handle)) {
			log.warn("Handle update failed: Handle {} already exists", handle);
			throw new IllegalArgumentException("Handle already in use.");
		}

		Profile profile = getProfileEntity(profileId);
		profile.setHandle(handle);

		Profile updatedProfile = this.profileRepository.save(profile);
		log.info("Handle for profile {} updated successfully to {}", profileId, handle);
		return ProfileResponse.fromProfile(updatedProfile);
	}

	/**
	 * Gets a profile by user ID.
	 *
	 * @param userId The ID of the user
	 * @return The profile
	 * @throws IllegalArgumentException If the profile doesn't exist
	 */
	public ProfileResponse getProfileByUserId(UUID userId) {
		log.info("Attempting to get profile for user {}", userId);
		Optional<Profile> profileOpt = this.profileRepository.findByUserId(userId);
		if (profileOpt.isEmpty()) {
			log.warn("Get profile failed: Profile for user {} not found", userId);
			throw new IllegalArgumentException("Profile not found.");
		}

		return ProfileResponse.fromProfile(profileOpt.get());
	}

	public ProfileResponse getProfile(UUID profileId) {
		Optional<Profile> profileOpt = this.profileRepository.findById(profileId);
		if (profileOpt.isEmpty()) {
			log.warn("Profile update failed: Profile {} not found", profileId);
			throw new IllegalArgumentException("Profile not found.");
		}

		return ProfileResponse.fromProfile(profileOpt.get());
	}

	private Profile getProfileEntity(UUID profileId) {
		Optional<Profile> profileOpt = this.profileRepository.findById(profileId);
		if (profileOpt.isEmpty()) {
			log.warn("Profile update failed: Profile {} not found", profileId);
			throw new IllegalArgumentException("Profile not found.");
		}

		return profileOpt.get();
	}
}
