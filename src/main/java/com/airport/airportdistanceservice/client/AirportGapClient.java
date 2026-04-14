package com.airport.airportdistanceservice.client;

import com.airport.airportdistanceservice.exception.AirportNotFoundException;
import com.airport.airportdistanceservice.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AirportGapClient {

    private final WebClient webClient;

    public AirportGapAirportResponse getAirportInfo(String iataCode){
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
}
