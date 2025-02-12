package de.tum.cit.aet.thesis.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key, int limit, Duration duration) {
        return buckets.computeIfAbsent(key, k -> createNewBucket(limit, duration));
    }

    private Bucket createNewBucket(int limit, Duration duration) {
        Bandwidth bandwidth = Bandwidth.classic(limit, Refill.intervally(limit, duration));
        return Bucket.builder().addLimit(bandwidth).build();
    }
}
