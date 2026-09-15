package org.nath.sns;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.nath.sns.config.TwitterModule;
import org.nath.sns.config.TwitterServiceConfiguration;
import org.nath.sns.dto.AuthenticatedUser;
import org.nath.sns.resource.TweetsResource;
import org.nath.sns.resource.TwitterServiceHealthResource;
import org.nath.sns.util.JwtAuthenticatorUtil;
import org.nath.sns.util.RoleAuthorizerUtil;
import org.nath.sns.util.UserContextFilter;
import org.nath.sns.manager.KafkaTweetsProducerManager;


public class TwitterService extends Application<TwitterServiceConfiguration>
{

    @Override
    public void initialize(Bootstrap<TwitterServiceConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    public static void main(String[] args) throws Exception {
        new TwitterService().run(args);
    }

    public void run(TwitterServiceConfiguration configuration, io.dropwizard.core.setup.Environment environment) {
        Injector injector = Guice.createInjector(
                new TwitterModule(configuration)
        );

        // Register UserContextFilter to capture Authenticated User
        environment.jersey().register(new UserContextFilter());

        environment.jersey().register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<AuthenticatedUser>()
                        .setAuthenticator(injector.getInstance(JwtAuthenticatorUtil.class))
                        .setAuthorizer(injector.getInstance(RoleAuthorizerUtil.class))
                        .setPrefix("Bearer")
                        .setRealm("API_SECURITY")
                        .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthenticatedUser.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

        environment.lifecycle().manage(injector.getInstance(KafkaTweetsProducerManager.class));

        environment.jersey().register(injector.getInstance(TwitterServiceHealthResource.class));
        environment.jersey().register(injector.getInstance(TweetsResource.class));

    }
}
