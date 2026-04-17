package com.airport.airportdistanceservice.client;

import com.airport.airportdistanceservice.exception.AirportNotFoundException;
import com.airport.airportdistanceservice.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AirportGapClient {

    private final WebClient webClient;

    @Retry(name = "airportGapApi", fallbackMethod = "getAirportInfoFallback")
    @CircuitBreaker(name = "airportGapApi", fallbackMethod = "getAirportInfoFallback")
    @Cacheable(value = "airports", key = "#iataCode", sync = true)
    public AirportGapAirportResponse getAirportInfo(String iataCode) {

        log.info("Fetching airport info for IATA code: {}", iataCode);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/airports/{iataCode}")
                        .build(iataCode))
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new AirportNotFoundException("Airport not found: " + iataCode))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new ExternalServiceException("External service error"))
                )
                // 0 ya da 1 nəticə
                .bodyToMono(AirportGapAirportResponse.class)
                .block();
    }

    private AirportGapAirportResponse getAirportInfoFallback(String iataCode, Throwable t) {
        log.warn("Circuit breaker fallback for: {}. Reason: {}", iataCode, t.getMessage());
        throw new ExternalServiceException(
                "Airport service is temporarily unavailable. Please try again later.", t);
    }
}
