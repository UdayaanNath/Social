package org.nath.sns.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;
import org.nath.sns.resource.TwitterServiceHealthResource;

public class TwitterModule extends AbstractModule
{
    private final SessionFactory sessionFactory;
    private final TwitterServiceConfiguration identityConfig;

    public TwitterModule(SessionFactory sessionFactory, TwitterServiceConfiguration identityConfig) {
        this.sessionFactory = sessionFactory;
        this.identityConfig = identityConfig;
    }

    @Override
    protected void configure() {
        // Bind your dependencies here

        bind(TwitterServiceHealthResource.class).in(Singleton.class);
    }
}
