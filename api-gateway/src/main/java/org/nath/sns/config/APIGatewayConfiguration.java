package org.nath.sns.config;

import io.dropwizard.core.Configuration;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class APIGatewayConfiguration extends Configuration {

    @NotEmpty
    private String identityServiceUrl = "http://localhost:8081";
    private RateLimitConfig rateLimitConfig;
    private JedisPoolConfiguration jedisPoolConfiguration;
    private RateLimitTokenBucketConfig rateLimitTokenBucketConfig;
}
