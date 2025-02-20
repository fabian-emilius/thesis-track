package de.tum.cit.aet.thesis.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for group management rate limiting.
 * Provides rate limiting buckets for group management operations.
 */
@Configuration
public class GroupRateLimitConfig {

    /**
     * Creates a rate limiting bucket for group management operations.
     * Limits requests to prevent abuse of group management endpoints.
     *
     * @return Bucket configured with rate limits
     */
    @Bean
    public Bucket groupManagementBucket() {
        // Allow 10 requests per minute for group management operations
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
