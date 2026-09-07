package org.nath.sns.filter.rateLimitStrategy;

import org.nath.sns.config.APIGatewayConfiguration;
import org.nath.sns.enums.RateLimitStrategy;
import redis.clients.jedis.JedisPool;

public class RateLimitStrategyFactory {
    public static RateLimitFilterStrategy getRateLimitFilterStrategy(
            RateLimitStrategy rateLimitStrategy,
            APIGatewayConfiguration apiGatewayConfiguration,
            JedisPool jedisPool) {
        RateLimitFilterStrategy rateLimitFilterStrategy;
        switch(rateLimitStrategy) {
            case TOKEN_BUCKET:
                rateLimitFilterStrategy = new TokenBucketRateLimitStrategy(apiGatewayConfiguration.getRateLimitTokenBucketConfig(), jedisPool);
            default:
                rateLimitFilterStrategy = new TokenBucketRateLimitStrategy(apiGatewayConfiguration.getRateLimitTokenBucketConfig(), jedisPool);
        }
        return rateLimitFilterStrategy;
    }
}
