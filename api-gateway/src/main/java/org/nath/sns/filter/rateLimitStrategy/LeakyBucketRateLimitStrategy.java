package org.nath.sns.filter.rateLimitStrategy;

public class LeakyBucketRateLimitStrategy implements RateLimitFilterStrategy{
    @Override
    public boolean filter(String clientId) {
        return false;
    }
}
