package com.airport.airportdistanceservice.filter;

import com.airport.airportdistanceservice.security.JwtService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${bucket4j.limit-per-minute}")
    private int limitPerMinute;

    private final JwtService jwtService;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        boolean allowed;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String email = jwtService.extractEmail(authHeader.substring(7));
                allowed = applyEmailRateLimit(email);
        } else {
            allowed = applyIpRateLimit(request);
        }

        if (!allowed) {
            response.sendError(429, "Too many requests. Please try again later. ex from bucket4j");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean applyIpRateLimit(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent("ip:" + ipAddress, key -> createBucket());
        return bucket.tryConsume(1);
    }

    private boolean applyEmailRateLimit(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        Bucket bucket = buckets.computeIfAbsent("email:" + email, key -> createBucket());
        return bucket.tryConsume(1);
    }

    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(limitPerMinute)
                        .refillGreedy(limitPerMinute, Duration.ofMinutes(1))) //her saniyede 1.67 sorgu elave olunur
                .build();
    }
}
