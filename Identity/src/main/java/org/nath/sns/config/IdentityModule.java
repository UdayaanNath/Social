package org.nath.sns.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;
import org.nath.sns.dao.UserDAO;
import org.nath.sns.resource.AppHealthResource;

public class IdentityModule extends AbstractModule {

    private final SessionFactory sessionFactory;
    private final IdentityConfig identityConfig;

    public IdentityModule(IdentityConfig identityConfig, SessionFactory sessionFactory) {
        this.identityConfig = identityConfig;
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void configure() {
        // Bind other specific interfaces here if needed
        bind(IdentityConfig.class).toInstance(identityConfig);
        bind(SessionFactory.class).toInstance(sessionFactory);

        bind(AppHealthResource.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public UserDAO provideUserDAO() {
        return new UserDAO(sessionFactory);
    }
}
