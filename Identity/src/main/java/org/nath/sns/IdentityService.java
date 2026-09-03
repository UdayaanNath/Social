package org.nath.sns;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.nath.sns.config.IdentityConfig;
import org.nath.sns.config.IdentityModule;
import org.nath.sns.dto.AuthenticatedUser;
import org.nath.sns.entity.UserEntity;
import org.nath.sns.entity.UserRoleEntity;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkApplicationListener;
import org.nath.sns.resource.AppHealthResource;
import org.nath.sns.resource.AuthResource;
import org.nath.sns.resource.UsersResource;
import org.nath.sns.resource.UserRolesResource;
import org.nath.sns.util.JwtAuthenticatorUtil;
import org.nath.sns.util.RoleAuthorizerUtil;
import org.nath.sns.util.UserContextFilter;


public class IdentityService extends Application<IdentityConfig>
{
    private final HibernateBundle<IdentityConfig> hibernateBundle =
            new HibernateBundle<IdentityConfig>(UserEntity.class, UserRoleEntity.class) {
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

        // Register UnitOfWork listener for Hibernate session management
        environment.jersey().register(new UnitOfWorkApplicationListener());
        // Register UserContextFilter to capture Authenticated User
        environment.jersey().register(new UserContextFilter());

        environment.jersey().register(injector.getInstance(AppHealthResource.class));
        environment.jersey().register(injector.getInstance(UsersResource.class));
        environment.jersey().register(injector.getInstance(UserRolesResource.class));
        environment.jersey().register(injector.getInstance(AuthResource.class));

        environment.jersey().register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<AuthenticatedUser>()
                        .setAuthenticator(injector.getInstance(JwtAuthenticatorUtil.class))
                        .setAuthorizer(injector.getInstance(RoleAuthorizerUtil.class))
                        .setPrefix("Bearer")
                        .setRealm("API_SECURITY")
                        .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthenticatedUser.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }
}
