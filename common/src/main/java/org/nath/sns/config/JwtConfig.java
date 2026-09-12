package org.nath.sns.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class JwtConfig {
    @NotEmpty @JsonProperty private String privateKeyPath;
    @NotEmpty @JsonProperty private String publicKeyPath;
    @NotNull @JsonProperty private Long expirationMs;

    public String getPrivateKeyPath() { return privateKeyPath; }
    public String getPublicKeyPath() { return publicKeyPath; }
    public Long getExpirationMs() { return expirationMs; }
}