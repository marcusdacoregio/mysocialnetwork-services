package com.dooall.mysocialnetwork.profiles;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Table("profile_follows")
public class ProfileFollow {

	@NotNull
	@Column("follower_profile_id")
	private UUID followerProfileId;

	@NotNull
	@Column("followee_profile_id")
	private UUID followeeProfileId;

	@NotNull
	@Column("followed_at")
	private OffsetDateTime followedAt;

	ProfileFollow() {

	}

	public ProfileFollow(UUID followerProfileId, UUID followeeProfileId, OffsetDateTime followedAt) {
		this.followerProfileId = followerProfileId;
		this.followeeProfileId = followeeProfileId;
		this.followedAt = followedAt;
	}

	public UUID getFollowerProfileId() {
		return this.followerProfileId;
	}

	public void setFollowerProfileId(UUID followerProfileId) {
		this.followerProfileId = followerProfileId;
	}

	public UUID getFolloweeProfileId() {
		return this.followeeProfileId;
	}

	public void setFolloweeProfileId(UUID followeeProfileId) {
		this.followeeProfileId = followeeProfileId;
	}

	public OffsetDateTime getFollowedAt() {
		return this.followedAt;
	}

	public void setFollowedAt(OffsetDateTime followedAt) {
		this.followedAt = followedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProfileFollow that = (ProfileFollow) o;
		return Objects.equals(this.followerProfileId, that.followerProfileId) &&
			   Objects.equals(this.followeeProfileId, that.followeeProfileId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.followerProfileId, this.followeeProfileId);
	}
}