package com.dooall.mysocialnetwork.users;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateUserResponse(
		UUID id,
		String email,
		OffsetDateTime createdAt
) {
}