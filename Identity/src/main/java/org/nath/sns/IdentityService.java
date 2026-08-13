package org.nath.sns;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import org.nath.sns.config.IdentityConfig;
import org.nath.sns.config.IdentityModule;
import org.nath.sns.entity.User;
import io.dropwizard.hibernate.HibernateBundle;
import org.nath.sns.resource.AppHealthResource;

public class IdentityService extends Application<IdentityConfig>
{
    private final HibernateBundle<IdentityConfig> hibernateBundle =
            new HibernateBundle<IdentityConfig>(User.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(IdentityConfig config) {
                    return config.getDataSourceFactory();
                }
            };

    @Override
    public void initialize(Bootstrap<IdentityConfig> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
    }

    public static void main( String[] args ) throws Exception {
        new IdentityService().run(args);
    }

    @Override
    public void run(IdentityConfig configuration, Environment environment) throws Exception {
        Injector injector = Guice.createInjector(
                new IdentityModule(configuration, hibernateBundle.getSessionFactory())
        );

        environment.jersey().register(injector.getInstance(AppHealthResource.class));
    }
}