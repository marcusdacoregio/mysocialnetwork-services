package com.dooall.mysocialnetwork.profiles;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileRegistrationRequest(
		@NotBlank(message = "Email is required")
		@Email(message = "Email should be valid")
		String email,

		@NotBlank(message = "Password is required")
		@Size(min = 8, message = "Password must be at least 8 characters long")
		String password,

		@NotBlank(message = "Display name is required")
		String displayName,

		@NotBlank(message = "Handle is required")
		@Size(max = 50, message = "Handle cannot exceed 50 characters")
		// Consider adding a pattern validation for handle if needed, e.g., alphanumeric
		String handle
) {
}