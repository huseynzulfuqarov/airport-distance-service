package com.airport.airportdistanceservice.controller;

import com.airport.airportdistanceservice.dto.airport.DistanceRequest;
import com.airport.airportdistanceservice.dto.airport.DistanceResponse;
import com.airport.airportdistanceservice.exception.TooManyRequestsException;
import com.airport.airportdistanceservice.service.DistanceService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/distances")
@RequiredArgsConstructor
public class DistanceController {
    private final DistanceService distanceService;

    @RateLimiter(name = "distanceApi", fallbackMethod = "rateLimitFallback")
    @PostMapping
    public ResponseEntity<DistanceResponse> calculateDistance(@Valid @RequestBody DistanceRequest request) {
        return ResponseEntity.ok(distanceService.calculateDistance(request));
    }

    private ResponseEntity<String> rateLimitFallback(DistanceRequest request, Throwable t) {

        throw new TooManyRequestsException("Too many requests. Please try again later.", t);
    }
}
