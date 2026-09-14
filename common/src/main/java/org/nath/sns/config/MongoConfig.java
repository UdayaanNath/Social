package org.nath.sns.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class MongoConfig {
    @NotEmpty @JsonProperty
    private String connectionString;
    @NotEmpty @JsonProperty
    private String databaseName;
}
