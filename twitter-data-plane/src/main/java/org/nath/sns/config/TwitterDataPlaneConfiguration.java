package org.nath.sns.config;

import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TwitterDataPlaneConfiguration extends Configuration {

    private MongoConfig mongoConfig;

    @Valid
    @NotNull
    private DataSourceFactory databaseSource = new DataSourceFactory();

    @Valid
    @NotNull
    private FlywayFactory flywayFactory = new FlywayFactory();
}
