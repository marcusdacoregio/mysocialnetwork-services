package com.dooall.mysocialnetwork.profiles;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a profile's display name and bio.
 */
public record UpdateProfileRequest(
        @Size(min = 1, max = 100, message = "Display name must be between 1 and 100 characters")
        String displayName,

        @Size(max = 500, message = "Bio must be less than 500 characters")
        String bio
) {
}
