package com.airport.airportdistanceservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
                .bodyToMono(AirportGapAirportResponse.class)
                .block();
    }
}
