package br.com.loginService.infrastructure.security.ratelimit;

import br.com.loginService.exception.ApplicationException;
import br.com.loginService.exception.ErrorEnum;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class EmailRateLimiter {

    private final ProxyManager<String> proxyManager;

    public EmailRateLimiter(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    public void check(String email) {
        try {
            Bucket bucket = proxyManager.getProxy(
                    buildKey(email),
                    this::emailLimitConfig
            );

            if (!bucket.tryConsume(1)) {
                throw new ApplicationException(ErrorEnum.RATE_LIMIT_EXCEEDED);
            }
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.warn("Redis unavailable, skipping email rate limit", e);
        }
    }

    private String buildKey(String email) {
        return "rate:login:email:" + email;
    }

    private BucketConfiguration emailLimitConfig() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(10)
                        .refillGreedy(10, Duration.ofMinutes(15)))
                .build();
    }
}