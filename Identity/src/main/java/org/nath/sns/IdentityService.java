package org.nath.sns;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import org.nath.sns.config.IdentityConfig;
import org.nath.sns.resource.AppHealthResource;

public class IdentityService extends Application<IdentityConfig>
{
    public static void main( String[] args ) throws Exception {
        new IdentityService().run(args);
    }

    @Override
    public void run(IdentityConfig configuration, Environment environment) throws Exception {
        environment.jersey().register(new AppHealthResource());
    }
}