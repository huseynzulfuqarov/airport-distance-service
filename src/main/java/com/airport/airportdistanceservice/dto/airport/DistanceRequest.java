package com.airport.airportdistanceservice.dto.airport;

import jakarta.validation.constraints.Pattern;

public record DistanceRequest(
        @Pattern(regexp = "^[A-Z]{3}$", message = "Origin airport IATA code must consist of 3 uppercase letters")
        String origin,

        @Pattern(regexp = "^[A-Z]{3}$", message = "Destination airport IATA code must consist of 3 uppercase letters")
        String destination
        ) {}
