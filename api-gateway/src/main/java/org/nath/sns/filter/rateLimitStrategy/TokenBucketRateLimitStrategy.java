package org.nath.sns.filter.rateLimitStrategy;

import org.nath.sns.config.RateLimitTokenBucketConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class TokenBucketRateLimitStrategy implements RateLimitFilterStrategy{

    private RateLimitTokenBucketConfig rateLimitTokenBucketConfig;
    private final JedisPool jedisPool;
    private final double refillRatePerMs;
    private final String luaScript;
    private final String scriptSha;

    public TokenBucketRateLimitStrategy(RateLimitTokenBucketConfig rateLimitTokenBucketConfig, JedisPool jedisPool) {
        this.rateLimitTokenBucketConfig = rateLimitTokenBucketConfig;
        this.jedisPool = jedisPool;
        this.refillRatePerMs = rateLimitTokenBucketConfig.getTokenPerSecond()/1000.0;
        this.luaScript = loadScriptFromClasspath(rateLimitTokenBucketConfig.getLuaScriptPath());

        // Preload SHA into Redis
        try (Jedis jedis = jedisPool.getResource()) {
            this.scriptSha = jedis.scriptLoad(this.luaScript);
        }
    }

    private String loadScriptFromClasspath(String path) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Lua script file not found on classpath: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Lua script from classpath: " + path, e);
        }
    }

    private boolean tryConsumeToken(String clientId) {
        String key = "rate_limit:" + clientId;
        long now = System.currentTimeMillis();
        boolean canConsume;

        List<String> keys = Collections.singletonList(key);
        List<String> args = List.of(
                String.valueOf(rateLimitTokenBucketConfig.getBucketCapacity()),
                String.valueOf(refillRatePerMs),
                String.valueOf(now),
                String.valueOf(1)
        );

        try (Jedis jedis = jedisPool.getResource()) {
            Object result;
            try {
                result = jedis.evalsha(scriptSha, keys, args);
            } catch (Exception e) {
                // Fallback if SHA is lost from Redis cache
                result = jedis.eval(luaScript, keys, args);
            }

            long leasedTokens = (Long) result;
            canConsume = leasedTokens > 0;
        }


        return canConsume;
    }

    @Override
    public boolean filter(String clientId) {
        return tryConsumeToken(clientId);
    }
}
