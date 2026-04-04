package com.server.configs.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {
    // Lưu bucket theo key (IP + API)
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /** 1 request mỗi 30 giây (vd: verify-email) */
    private static final List<String> ROUTES_1_PER_30_SECONDS = Arrays.asList(

    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        if (ROUTES_1_PER_30_SECONDS.stream().anyMatch(path::startsWith)) {
            log.debug("Rate limit 30s applied for path: {}", path);
            handleRateLimit(request, response, chain,
                    1, Duration.ofSeconds(30));
            return;
        }

        chain.doFilter(request, response);
    }

    private void handleRateLimit(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            int limit,
            Duration duration)
            throws IOException, ServletException {

        String key = request.getRemoteAddr()
                + ":" + request.getRequestURI();

        Bucket bucket = buckets.computeIfAbsent(key, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(
                        limit,
                        Refill.intervally(limit, duration)))
                .build());

        if (bucket.tryConsume(1)) {
            log.debug("Rate limit passed for key: {}, remaining tokens: {}", key, bucket.getAvailableTokens());
            chain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for key: {}", key);
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too Many Requests\"}");
            return;
        }
    }

}
