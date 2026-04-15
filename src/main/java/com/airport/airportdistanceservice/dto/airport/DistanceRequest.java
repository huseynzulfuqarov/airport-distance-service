package com.airport.airportdistanceservice.dto.airport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DistanceRequest(
        @NotBlank(message = "{validation.origin.required}")
        @Pattern(regexp = "^[A-Z]{3}$", message = "{validation.origin.pattern}")
        String origin,

        @NotBlank(message = "{validation.destination.required}")
        @Pattern(regexp = "^[A-Z]{3}$", message = "{validation.destination.pattern}")
        String destination
) {}
