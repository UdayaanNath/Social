package org.nath.sns.service;

import org.nath.sns.dao.TweetMetadataDao;
import org.nath.sns.entity.TweetEntity;
import org.nath.sns.entity.TweetMetadataEntity;
import org.nath.sns.enums.TweetMetadataStatus;

public class TweetMetadataService {
    private final TweetMetadataDao tweetMetadataDao;

    public TweetMetadataService(TweetMetadataDao tweetMetadataDao) {
        this.tweetMetadataDao = tweetMetadataDao;
    }

    public void createTweetMetadata(TweetEntity tweetEntity) {
        TweetMetadataEntity tweetMetadataEntity = TweetMetadataEntity.builder()
                .id(tweetEntity.getId())
                .authorId(tweetEntity.getAuthorId())
                .createdAt(tweetEntity.getCreateAt())
                .updatedAt(tweetEntity.getUpdateAt())
                .status(TweetMetadataStatus.ACTIVE)
                .build();
        tweetMetadataDao.create(tweetMetadataEntity);
    }
}
