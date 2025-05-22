package com.dooall.mysocialnetwork.profiles.controller;

import com.dooall.mysocialnetwork.core.AuthenticatedUser;
import com.dooall.mysocialnetwork.core.CurrentUser;
import com.dooall.mysocialnetwork.profiles.Profile;
import com.dooall.mysocialnetwork.profiles.ProfileResponse;
import com.dooall.mysocialnetwork.profiles.ProfileService;
import com.dooall.mysocialnetwork.profiles.UpdateHandleRequest;
import com.dooall.mysocialnetwork.profiles.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Updates a profile's display name and bio.
     * Only the profile owner can update their profile.
     *
     * @param profileId The ID of the profile to update
     * @param request   The update request containing userId, displayName, and bio
     * @return The updated profile
     */
    @PreAuthorize("@profileAuthz.isProfileOwner(#authenticatedUser.userId, #profileId)")
    @PutMapping("/{profileId}")
    public ResponseEntity<?> updateProfile(@PathVariable UUID profileId, @Valid @RequestBody UpdateProfileRequest request,
                                           @CurrentUser AuthenticatedUser authenticatedUser) {
        log.info("Received request to update profile {} from user {}", profileId, authenticatedUser.getUserId());
        try {
            Profile updatedProfile = this.profileService.updateProfile(
                    profileId,
                    authenticatedUser.getUserId(),
                    request.displayName(),
                    request.bio()
            );
            return ResponseEntity.ok(ProfileResponse.fromProfile(updatedProfile));
        } catch (IllegalArgumentException e) {
            log.warn("Profile update failed due to bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            log.warn("Profile update failed due to authorization: {}", e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred during profile update: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An unexpected error occurred during profile update.");
        }
    }

    /**
     * Updates a profile's handle.
     * Only the profile owner can update their handle.
     * The handle must be unique.
     *
     * @param profileId The ID of the profile to update
     * @param request   The update request containing userId and handle
     * @return The updated profile
     */
    @PreAuthorize("@profileAuthz.isProfileOwner(#authenticatedUser.userId, #profileId)")
    @PutMapping("/{profileId}/handle")
    public ResponseEntity<?> updateHandle(@PathVariable UUID profileId, @Valid @RequestBody UpdateHandleRequest request,
                                          @CurrentUser AuthenticatedUser authenticatedUser) {
        log.info("Received request to update handle for profile {} to {} from user {}", profileId, request.handle(), authenticatedUser.getUserId());
        try {
            Profile updatedProfile = this.profileService.updateHandle(
                    profileId,
                    authenticatedUser.getUserId(),
                    request.handle()
            );
            return ResponseEntity.ok(ProfileResponse.fromProfile(updatedProfile));
        } catch (IllegalArgumentException e) {
            log.warn("Handle update failed due to bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            log.warn("Handle update failed due to authorization: {}", e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred during handle update: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An unexpected error occurred during handle update.");
        }
    }
}