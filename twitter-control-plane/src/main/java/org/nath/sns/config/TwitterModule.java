package org.nath.sns.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.nath.sns.resource.TwitterServiceHealthResource;

public class TwitterModule extends AbstractModule
{
    private final TwitterServiceConfiguration twitterServiceConfiguration;

    public TwitterModule(TwitterServiceConfiguration twitterServiceConfiguration) {
        this.twitterServiceConfiguration = twitterServiceConfiguration;
    }

    @Override
    protected void configure() {
        // Bind your dependencies here
        bind(TwitterServiceConfiguration.class).toInstance(twitterServiceConfiguration);

        bind(TwitterServiceHealthResource.class).in(Singleton.class);
    }
}
