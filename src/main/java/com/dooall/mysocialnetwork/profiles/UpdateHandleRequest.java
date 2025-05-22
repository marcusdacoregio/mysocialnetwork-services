package com.dooall.mysocialnetwork.profiles;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for updating a profile's handle.
 */
public record UpdateHandleRequest(
        @NotBlank
        @Size(max = 50, message = "Handle must be less than 50 characters")
        String handle
) {
}