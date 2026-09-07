package org.nath.sns.filter.rateLimitStrategy;

public class SlidingWindowRateLimitStrategy implements RateLimitFilterStrategy {

    @Override
    public boolean filter(String clientId) {
        return false;
    }
}
