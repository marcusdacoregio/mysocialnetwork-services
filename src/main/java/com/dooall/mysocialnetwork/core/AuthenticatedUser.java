package com.dooall.mysocialnetwork.core;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class AuthenticatedUser implements UserDetails {

	private final String email;
	private final String password;
	private final Set<SimpleGrantedAuthority> authorities;

	private UUID userId;
	private UUID profileId;

	public AuthenticatedUser(String email, String password) {
		Assert.hasText(email, "email cannot be empty");
		Assert.hasText(password, "password cannot be empty");
		this.email = email;
		this.password = password;
		this.authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	public UUID getUserId() {
		return this.userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getProfileId() {
		return this.profileId;
	}

	public void setProfileId(UUID profileId) {
		this.profileId = profileId;
	}

}
