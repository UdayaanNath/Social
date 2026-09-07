package org.nath.sns.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.nath.sns.enums.RateLimitStrategy;

@Getter
@Setter
public class RateLimitConfig {
    @NotEmpty
    private boolean enabled = false;
    @NotBlank
    private String rateLimitStrategy;
}
