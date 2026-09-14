package org.nath.sns.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import lombok.Getter;

@Getter
public class TwitterServiceConfiguration extends Configuration {

    @JsonProperty("jwt")
    private JwtConfig jwt = new JwtConfig();
}
