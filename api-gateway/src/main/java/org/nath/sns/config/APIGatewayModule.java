package org.nath.sns.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.nath.sns.filter.RateLimitFilter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class APIGatewayModule extends AbstractModule {
    private APIGatewayConfiguration apiGatewayConfiguration;

    public APIGatewayModule(APIGatewayConfiguration apiGatewayConfiguration) {
        this.apiGatewayConfiguration = apiGatewayConfiguration;
    }

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    public JedisPool getJedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(apiGatewayConfiguration.getJedisPoolConfiguration().getMaxTotal());
        return new JedisPool(
                poolConfig,
                apiGatewayConfiguration.getJedisPoolConfiguration().getEndpoint(),
                apiGatewayConfiguration.getJedisPoolConfiguration().getPort());
    }

    @Provides
    @Singleton
    public RateLimitFilter getRateLimitFilter(JedisPool jedisPool) {
        return new RateLimitFilter(apiGatewayConfiguration, jedisPool);
    }
}
