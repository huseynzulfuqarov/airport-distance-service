package com.airport.airportdistanceservice.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.valid}")
        String email,

        @NotBlank(message = "{validation.password.required}")
        String password
) {}
