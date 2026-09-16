package org.nath.sns.service;

import org.nath.sns.dao.TweetDao;
import org.nath.sns.entity.TweetEntity;

public class TweetService {

    private final TweetDao tweetDao;

    public TweetService(TweetDao tweetDao) {
        this.tweetDao = tweetDao;
    }

    public TweetEntity createTweet(String content, Long authorId) {
        // Create a TweetEntity and save it using the DAO
        TweetEntity tweetEntity = TweetEntity.builder()
                .content(content)
                .authorId(authorId)
                .createAt(System.currentTimeMillis())
                .updateAt(System.currentTimeMillis())
                .build();

        return tweetDao.createTweet(tweetEntity);
    }
}
