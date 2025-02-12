package de.tum.cit.aet.thesis.aspect;

import de.tum.cit.aet.thesis.annotation.RateLimit;
import de.tum.cit.aet.thesis.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Aspect
@Component
public class RateLimitAspect {
    private final RateLimitConfig rateLimitConfig;

    public RateLimitAspect(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = joinPoint.getSignature().toLongString();
        Duration duration = Duration.of(1, rateLimit.timeUnit().toChronoUnit());
        Bucket bucket = rateLimitConfig.resolveBucket(key, rateLimit.limit(), duration);

        if (!bucket.tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }

        return joinPoint.proceed();
    }
}
