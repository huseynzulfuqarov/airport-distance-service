package com.airport.airportdistanceservice.service;

import com.airport.airportdistanceservice.dto.auth.AuthResponse;
import com.airport.airportdistanceservice.dto.auth.LoginRequest;
import com.airport.airportdistanceservice.dto.auth.RegisterRequest;

public interface AuthenticationService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String authHeader);
}