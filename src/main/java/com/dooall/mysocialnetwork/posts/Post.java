package com.dooall.mysocialnetwork.posts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Table("posts")
public class Post {

	@Id
	@NotNull
	private UUID id;

	@NotNull
	@Column("author_profile_id")
	private UUID authorProfileId;

	@NotBlank
	@NotNull
	@Column("content")
	private String content;

	@NotBlank
	@Column("image_url")
	private String imageUrl;

	@NotNull
	@Column("like_count")
	private Integer likeCount = 0;

	@NotNull
	@Column("comment_count")
	private Integer commentCount = 0;

	@NotNull
	@Column("created_at")
	private OffsetDateTime createdAt;

	Post() {

	}

	public Post(UUID id, UUID authorProfileId, String content) {
		this.id = id;
		this.authorProfileId = authorProfileId;
		this.content = content;
		this.createdAt = OffsetDateTime.now();
	}

	public UUID getId() {
		return this.id;
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

	public String getImageUrl() {
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getLikeCount() {
		return this.likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public Integer getCommentCount() {
		return this.commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
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
		Post post = (Post) o;
		return Objects.equals(this.id, post.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id);
	}
}