package org.nath.sns.filter.rateLimitStrategy;

public interface RateLimitFilterStrategy {
    boolean filter(String clientId);
}
