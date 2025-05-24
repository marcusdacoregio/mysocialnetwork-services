package com.dooall.mysocialnetwork.profiles.controller;

import com.dooall.mysocialnetwork.core.AuthenticatedUser;
import com.dooall.mysocialnetwork.core.CurrentUser;
import com.dooall.mysocialnetwork.profiles.ProfileResponse;
import com.dooall.mysocialnetwork.profiles.ProfileService;
import com.dooall.mysocialnetwork.profiles.UpdateHandleRequest;
import com.dooall.mysocialnetwork.profiles.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
    @PreAuthorize("@profileAuthz.canEdit(#authenticatedUser.userId, #profileId)")
    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> updateProfile(@PathVariable UUID profileId, @Valid @RequestBody UpdateProfileRequest request,
                                           @CurrentUser AuthenticatedUser authenticatedUser) {
        log.info("Received request to update profile {} from user {}", profileId, authenticatedUser.getUserId());
        ProfileResponse updatedProfile = this.profileService.updateProfile(
                profileId,
                authenticatedUser.getUserId(),
                request.displayName(),
                request.bio()
        );
        return ResponseEntity.ok(updatedProfile);
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
    @PreAuthorize("@profileAuthz.canEdit(#authenticatedUser.userId, #profileId)")
    @PutMapping("/{profileId}/handle")
    public ResponseEntity<ProfileResponse> updateHandle(@PathVariable UUID profileId, @Valid @RequestBody UpdateHandleRequest request,
                                          @CurrentUser AuthenticatedUser authenticatedUser) {
        log.info("Received request to update handle for profile {} to {} from user {}", profileId, request.handle(), authenticatedUser.getUserId());
        ProfileResponse updatedProfile = this.profileService.updateHandle(
                profileId,
                authenticatedUser.getUserId(),
                request.handle()
        );
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Gets the profile of the currently authenticated user.
     *
     * @param authenticatedUser The authenticated user
     * @return The user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getCurrentUserProfile(@CurrentUser AuthenticatedUser authenticatedUser) {
        ProfileResponse profile = this.profileService.getProfile(authenticatedUser.getProfileId());
        return ResponseEntity.ok(profile);
    }
}
