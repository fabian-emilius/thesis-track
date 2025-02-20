package de.tum.cit.aet.thesis.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for rate limiting different API operations.
 * Uses Bucket4j to implement token bucket algorithm for rate limiting.
 * Limits are configurable through application.yml properties.
 */
@Configuration
public class RateLimitConfig {

    private final RateLimitProperties rateLimitProperties;

    public RateLimitConfig(RateLimitProperties rateLimitProperties) {
        this.rateLimitProperties = rateLimitProperties;
    }

    /**
     * Creates a rate limiter bucket with specified limits.
     * @param limit Number of requests allowed in the time window
     * @param windowMinutes Time window in minutes
     * @return Configured rate limit bucket
     */
    private Bucket createBucket(int limit, int windowMinutes) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(limit, Refill.greedy(limit, Duration.ofMinutes(windowMinutes))))
                .build();
    }

    /**
     * Creates a rate limiter for group management operations.
     * @return Bucket configured with rate limits for group operations
     */
    @Bean
    public Bucket groupManagementBucket() {
        return createBucket(
            rateLimitProperties.getGroupOperationsLimit(),
            rateLimitProperties.getGroupOperationsWindow()
        );
    }

    /**
     * Creates a rate limiter for user operations.
     * @return Bucket configured with rate limits for user operations
     */
    @Bean
    public Bucket userOperationsBucket() {
        return createBucket(
            rateLimitProperties.getUserOperationsLimit(),
            rateLimitProperties.getUserOperationsWindow()
        );
    }

    /**
     * Creates a rate limiter for application operations.
     * @return Bucket configured with rate limits for application operations
     */
    @Bean
    public Bucket applicationOperationsBucket() {
        return createBucket(
            rateLimitProperties.getApplicationOperationsLimit(),
            rateLimitProperties.getApplicationOperationsWindow()
        );
    }
}
