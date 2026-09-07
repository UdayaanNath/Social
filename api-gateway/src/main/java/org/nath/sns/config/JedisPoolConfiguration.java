package org.nath.sns.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JedisPoolConfiguration {

    private Integer maxTotal = 128;
    @NotEmpty
    private String endpoint;
    @NotEmpty
    private Integer port;
}
