package com.airport.airportdistanceservice.service.impl;

import com.airport.airportdistanceservice.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    @Override
    public void addToBlacklist(String token, long expirationInMilliseconds) {
        String key = BLACKLIST_PREFIX + token;
        log.info("Token blacklisted, TTL: {}ms", expirationInMilliseconds);
        stringRedisTemplate.opsForValue().set(key, "true", expirationInMilliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
