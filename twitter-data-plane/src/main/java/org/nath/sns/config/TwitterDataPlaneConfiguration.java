package org.nath.sns.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TwitterDataPlaneConfiguration extends Configuration {

    @JsonProperty("mongo")
    private MongoConfig mongoConfig;

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory databaseSource = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty("flyway")
    private FlywayFactory flywayFactory = new FlywayFactory();

    @JsonProperty("kafka")
    private KafkaConfig kafka = new KafkaConfig();
}
