package com.dooall.mysocialnetwork.users;

import com.fasterxml.uuid.Generators;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Table("users")
public class User {

	@Id
	@NotNull
	private UUID id;

	@NotNull
	@NotBlank
	@Size(max = 255)
	private String email;

	@NotNull
	@NotBlank
	@Column("password_hash")
	private String passwordHash;

	@NotNull
	@Column("created_at")
	private OffsetDateTime createdAt;

	@Column("deleted_at")
	private OffsetDateTime deletedAt;

	User() {

	}

	public User(String email, String passwordHash) {
		this.id = Generators.timeBasedEpochGenerator().generate();
		this.email = email;
		this.passwordHash = passwordHash;
		this.createdAt = OffsetDateTime.now();
	}

	public UUID getId() {
		return this.id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return this.passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public OffsetDateTime getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public OffsetDateTime getDeletedAt() {
		return this.deletedAt;
	}

	public void setDeletedAt(OffsetDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(this.id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id);
	}
}
