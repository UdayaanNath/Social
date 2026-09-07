package org.nath.sns.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nath.sns.config.APIGatewayConfiguration;
import org.nath.sns.filter.rateLimitStrategy.RateLimitFilterStrategy;
import org.nath.sns.filter.rateLimitStrategy.RateLimitStrategyFactory;
import org.nath.sns.enums.RateLimitStrategy;
import java.util.Locale;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

public class RateLimitFilter implements Filter {

    private RateLimitFilterStrategy rateLimitFilterStrategy;

    public RateLimitFilter(APIGatewayConfiguration apiGatewayConfiguration, JedisPool jedisPool) {
        String configuredStrategy = apiGatewayConfiguration
                .getRateLimitConfig()
                .getRateLimitStrategy();

        RateLimitStrategy strategy;
        try {
            strategy = RateLimitStrategy.valueOf(
                    configuredStrategy.trim().toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unsupported rateLimitStrategy: " + configuredStrategy,
                    exception
            );
        }
        this.rateLimitFilterStrategy = RateLimitStrategyFactory.getRateLimitFilterStrategy(strategy, apiGatewayConfiguration, jedisPool);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String apiKey = httpRequest.getHeader("X-API-Key");

        if (apiKey == null || apiKey.isBlank()) {
            writeError(httpResponse, HttpServletResponse.SC_BAD_REQUEST,
                    "{\"error\": \"Bad Request\", \"message\": \"X-API-Key header is required.\"}");
            return;
        }

        boolean allowRequest = rateLimitFilterStrategy.filter(apiKey);

        if (!allowRequest) {
            writeError(httpResponse, 429,
                    "{\"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(body);
    }
}
