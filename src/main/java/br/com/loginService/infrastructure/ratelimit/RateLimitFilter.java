package br.com.loginService.infrastructure.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final LettuceBasedProxyManager<String> proxyManager;

    public RateLimitFilter(LettuceBasedProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String ip = extractClientIp(request);

        try {
            Bucket bucket = proxyManager.getProxy("rate:login:ip:" + ip, this::ipLimitConfig);

            if (bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"RATE_LIMIT_EXCEEDED\"}");
            }
        } catch (Exception e) {
            log.warn("Rate limit unavailable, continuing without throttling: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

    private BucketConfiguration ipLimitConfig() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(20)
                        .refillGreedy(20, Duration.ofMinutes(15)))
                .addLimit(limit -> limit.capacity(5)
                        .refillGreedy(5, Duration.ofSeconds(10)))
                .build();
    }

    private String extractClientIp(HttpServletRequest request) {
        String cfIp = request.getHeader("CF-Connecting-IP");
        return (cfIp != null && !cfIp.isBlank()) ? cfIp : request.getRemoteAddr();
    }
}