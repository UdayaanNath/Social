CREATE TABLE IF NOT EXISTS tweet_metadata (
    id VARCHAR(36) PRIMARY KEY,
    author_id BIGINT NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL,
    CONSTRAINT fk_tweet_metadata_user FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);
