package com.dooall.mysocialnetwork.profiles;

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

@Table("profiles")
public class Profile {

	@Id
	@NotNull
	private UUID id;

	@NotNull
	@Column("user_id")
	private UUID userId;

	@NotBlank
	@NotNull
	@Column("display_name")
	private String displayName;

	@NotBlank
	@NotNull
	@Size(max = 50)
	@Column("handle")
	private String handle;

	@Column("bio")
	private String bio;

	@NotNull
	@Column("created_at")
	private OffsetDateTime createdAt;

	@Column("deleted_at")
	private OffsetDateTime deletedAt;

	Profile() {

	}

	public Profile(UUID userId, String displayName, String handle) {
		this.id = Generators.timeBasedEpochGenerator().generate();
		this.userId = userId;
		this.displayName = displayName;
		this.handle = handle;
		this.createdAt = OffsetDateTime.now();
	}

	public UUID getId() {
		return this.id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return this.userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getHandle() {
		return this.handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getBio() {
		return this.bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
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
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Profile profile = (Profile) o;
		return Objects.equals(this.id, profile.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id);
	}
}