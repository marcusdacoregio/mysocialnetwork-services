package com.dooall.mysocialnetwork.posts;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Table("post_likes")
public class PostLike {

	@NotNull
	@Column("user_profile_id")
	private UUID userProfileId;

	@NotNull
	@Column("post_id")
	private UUID postId;

	@NotNull
	@Column("liked_at")
	private OffsetDateTime likedAt;

	PostLike() {

	}

	public PostLike(UUID userProfileId, UUID postId, OffsetDateTime likedAt) {
		this.userProfileId = userProfileId;
		this.postId = postId;
		this.likedAt = likedAt;
	}

	public UUID getUserProfileId() {
		return this.userProfileId;
	}

	public void setUserProfileId(UUID userProfileId) {
		this.userProfileId = userProfileId;
	}

	public UUID getPostId() {
		return this.postId;
	}

	public void setPostId(UUID postId) {
		this.postId = postId;
	}

	public OffsetDateTime getLikedAt() {
		return this.likedAt;
	}

	public void setLikedAt(OffsetDateTime likedAt) {
		this.likedAt = likedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PostLike postLike = (PostLike) o;
		return Objects.equals(this.userProfileId, postLike.userProfileId) &&
			   Objects.equals(this.postId, postLike.postId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.userProfileId, this.postId);
	}
}