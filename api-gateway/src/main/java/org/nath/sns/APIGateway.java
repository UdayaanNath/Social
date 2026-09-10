package org.nath.sns;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import org.eclipse.jetty.proxy.AsyncMiddleManServlet;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletRegistration;
import org.nath.sns.config.APIGatewayConfiguration;
import org.nath.sns.config.APIGatewayModule;
import org.nath.sns.filter.RateLimitFilter;
import redis.clients.jedis.JedisPool;

import java.util.EnumSet;

public class APIGateway extends Application<APIGatewayConfiguration>
{
    public static void main( String[] args ) throws Exception
    {
        new APIGateway().run(args);
    }

    @Override
    public void initialize(Bootstrap<APIGatewayConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    @Override
    public void run(APIGatewayConfiguration config, Environment environment) {
        Injector injector = Guice.createInjector(
                new APIGatewayModule(config)
        );

        environment.lifecycle().manage(new io.dropwizard.lifecycle.Managed() {
            @Override
            public void start() {}

            @Override
            public void stop() {
                injector.getInstance(JedisPool.class).close();
            }
        });

        FilterRegistration.Dynamic rateLimitFilter = environment.servlets().addFilter(
                "RateLimitFilter", injector.getInstance(RateLimitFilter.class)
        );
        rateLimitFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

        // Register Jetty Async Proxy Servlet for Transparent Request Forwarding
        ServletRegistration.Dynamic proxyServlet = environment.servlets().addServlet(
                "GatewayProxyServlet",
                new AsyncMiddleManServlet.Transparent()
        );

        // Map paths (e.g., http://gateway:8082/identity/* -> http://identity:8080/*)
        proxyServlet.addMapping("/identity/*");
        proxyServlet.setInitParameter("proxyTo", config.getIdentityServiceUrl());
        proxyServlet.setInitParameter("prefix", "/identity");

        environment.jersey().register(injector.getInstance(org.nath.sns.resource.GatewayHealthResource.class));
    }
}
