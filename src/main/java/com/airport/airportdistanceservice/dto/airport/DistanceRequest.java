package com.airport.airportdistanceservice.dto.airport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DistanceRequest(
        @NotBlank(message = "Origin airport IATA code is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Origin airport IATA code must consist of 3 uppercase letters")
        String origin,

        @NotBlank(message = "Destination airport IATA code is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Destination airport IATA code must consist of 3 uppercase letters")
        String destination
) {}
