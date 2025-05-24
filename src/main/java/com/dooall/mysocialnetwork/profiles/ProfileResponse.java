package com.dooall.mysocialnetwork.profiles;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for profile information.
 */
public record ProfileResponse(
        UUID id,
        UUID userId,
        String displayName,
        String handle,
        String bio,
        OffsetDateTime createdAt
) {
    /**
     * Creates a ProfileResponse from a Profile entity.
     */
    static ProfileResponse fromProfile(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getDisplayName(),
                profile.getHandle(),
                profile.getBio(),
                profile.getCreatedAt()
        );
    }
}