package com.dooall.mysocialnetwork.posts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Table("post_comments")
public class PostComment {

	@Id
	@NotNull
	private UUID id;

	@NotNull
	@Column("post_id")
	private UUID postId;

	@NotNull
	@Column("author_profile_id")
	private UUID authorProfileId;

	@NotBlank
	@NotNull
	@Column("content")
	private String content;

	@NotNull
	@Column("created_at")
	private OffsetDateTime createdAt;

	PostComment() {

	}

	public PostComment(UUID id, UUID postId, UUID authorProfileId, String content) {
		this.id = id;
		this.postId = postId;
		this.authorProfileId = authorProfileId;
		this.content = content;
		this.createdAt = OffsetDateTime.now();
	}

	public UUID getId() {
		return this.id;
	}

	public UUID getPostId() {
		return this.postId;
	}

	public void setPostId(UUID postId) {
		this.postId = postId;
	}

	public UUID getAuthorProfileId() {
		return this.authorProfileId;
	}

	public void setAuthorProfileId(UUID authorProfileId) {
		this.authorProfileId = authorProfileId;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public OffsetDateTime getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PostComment that = (PostComment) o;
		return Objects.equals(this.id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id);
	}
}