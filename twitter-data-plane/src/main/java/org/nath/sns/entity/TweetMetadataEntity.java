package org.nath.sns.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.nath.sns.enums.TweetMetadataStatus;

@Entity
@Table(name = "tweet_metadata")
@Setter
@Getter
public class TweetMetadataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private TweetMetadataStatus status;
}
