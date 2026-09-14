package org.nath.sns.entity;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TweetEntity {
    private long id;
    @Size(max=2048, message = "Content must be less than or equal to 2048 characters")
    private String content;
    private long authorId;
    private long createAt;
    private long updateAt;
}
