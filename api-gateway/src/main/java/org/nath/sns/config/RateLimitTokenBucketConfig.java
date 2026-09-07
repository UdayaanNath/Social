package org.nath.sns.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateLimitTokenBucketConfig {
    private Integer bucketCapacity;
    private Integer tokenPerSecond;
    private String luaScriptPath;
}
