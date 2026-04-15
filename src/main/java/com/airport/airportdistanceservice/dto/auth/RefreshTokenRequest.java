package com.airport.airportdistanceservice.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "{validation.refreshToken.required}")
        String refreshToken
) {}
