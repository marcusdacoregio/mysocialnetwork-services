CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    email         TEXT UNIQUE NOT NULL,
    password_hash TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);

-- PROFILES (social identities, multiple per user)
CREATE TABLE profiles
(
    id           UUID PRIMARY KEY,
    user_id      UUID               NOT NULL REFERENCES users (id),
    display_name TEXT               NOT NULL,
    handle       VARCHAR(50) UNIQUE NOT NULL,
    bio          TEXT,
    created_at   TIMESTAMPTZ        NOT NULL DEFAULT now(),
    deleted_at   TIMESTAMPTZ
);

CREATE TABLE profile_follows
(
    follower_profile_id UUID        NOT NULL REFERENCES profiles (id),
    followee_profile_id UUID        NOT NULL REFERENCES profiles (id),
    followed_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (follower_profile_id, followee_profile_id)
);

CREATE TABLE posts
(
    id                UUID PRIMARY KEY,
    author_profile_id UUID        NOT NULL REFERENCES profiles (id),
    content           TEXT        NOT NULL,
    image_url         TEXT        NOT NULL,
    like_count        INTEGER     NOT NULL DEFAULT 0,
    comment_count     INTEGER     NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- POST LIKES (profiles liking posts)
CREATE TABLE post_likes
(
    user_profile_id UUID        NOT NULL REFERENCES profiles (id),
    post_id         UUID        NOT NULL REFERENCES posts (id),
    liked_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_profile_id, post_id)
);

-- POST COMMENTS (comments on posts by profiles)
CREATE TABLE post_comments
(
    id                UUID PRIMARY KEY,
    post_id           UUID        NOT NULL REFERENCES posts (id),
    author_profile_id UUID        NOT NULL REFERENCES profiles (id),
    content           TEXT        NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_profiles_user_id ON profiles (user_id);
CREATE INDEX idx_profiles_deleted_at ON profiles (deleted_at); -- For soft-delete filtering

CREATE INDEX idx_follows_followee_profile_id ON profile_follows (followee_profile_id);
CREATE INDEX idx_follows_follower_profile_id ON profile_follows (follower_profile_id);

CREATE INDEX idx_posts_author_profile_id ON posts (author_profile_id);
CREATE INDEX idx_posts_created_at ON posts (created_at DESC);

CREATE INDEX idx_post_likes_post_id ON post_likes (post_id);
CREATE INDEX idx_post_likes_user_profile_id ON post_likes (user_profile_id);

CREATE INDEX idx_post_comments_post_id ON post_comments (post_id);
CREATE INDEX idx_post_comments_author_profile_id ON post_comments (author_profile_id);
CREATE INDEX idx_post_comments_created_at ON post_comments (created_at DESC);
