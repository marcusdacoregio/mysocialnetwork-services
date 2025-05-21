package com.dooall.mysocialnetwork.profiles;

import java.util.UUID;

public record ProfileRegistrationResponse(
		UUID userId,
		UUID profileId,
		String email,
		String displayName,
		String handle
) {
}