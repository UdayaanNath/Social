package org.nath.sns.dao;

import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;
import org.nath.sns.entity.TweetMetadataEntity;

public class TweetMetadataDao extends AbstractDAO<TweetMetadataEntity> {

    public TweetMetadataDao(SessionFactory factory) {
        super(factory);
    }

    @UnitOfWork
    public TweetMetadataEntity create(TweetMetadataEntity tweetMetadataEntity) {
        return persist(tweetMetadataEntity);
    }

    @UnitOfWork
    public TweetMetadataEntity findById(String id) {
        return get(id);
    }
}
