package com.airport.airportdistanceservice.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "{validation.firstName.required}")
        String firstName,

        @NotBlank(message = "{validation.lastName.required}")
        String lastName,

        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.valid}")
        String email,

        @NotBlank(message = "{validation.password.required}")
        @Size(min = 8, message = "{validation.password.size}")
        String password
) {}