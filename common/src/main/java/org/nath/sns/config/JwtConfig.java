package org.nath.sns.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class JwtConfig {
    @NotEmpty @JsonProperty private String privateKeyPath;
    @NotEmpty @JsonProperty private String publicKeyPath;
    @NotNull @JsonProperty private Long expirationMs;
}