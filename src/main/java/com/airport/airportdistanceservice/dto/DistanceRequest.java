package com.airport.airportdistanceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DistanceRequest(
        @NotBlank(message = "Origin airport code is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Origin airport IATA code must consist of 3 uppercase letters")
        String origin,

        @NotBlank(message = "Destination  airport code is required")
        @Size(min = 3, max = 3, message = "Destination  airport IATA code must be exactly 3 characters")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Destination airport IATA code must consist of 3 uppercase letters")
        String destination
        ) {}
