package com.airport.airportdistanceservice.service;

public interface TokenBlacklistService {
    void addToBlacklist(String token, long expirationInMilliseconds);
    boolean isBlacklisted(String token);
}