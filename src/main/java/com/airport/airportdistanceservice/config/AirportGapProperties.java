package com.airport.airportdistanceservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "airportgap.api")
public record AirportGapProperties(String baseUrl) {
}
