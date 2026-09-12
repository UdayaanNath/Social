package org.nath.sns.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tweets")
@Setter
@Getter
public class TweetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A no-argument constructor is required by Hibernate
    public TweetEntity() {}
}
