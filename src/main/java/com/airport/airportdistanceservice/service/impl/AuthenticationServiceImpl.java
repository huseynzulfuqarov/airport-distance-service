package com.airport.airportdistanceservice.service.impl;

import com.airport.airportdistanceservice.dto.auth.AuthResponse;
import com.airport.airportdistanceservice.dto.auth.LoginRequest;
import com.airport.airportdistanceservice.dto.auth.RefreshTokenRequest;
import com.airport.airportdistanceservice.dto.auth.RegisterRequest;
import com.airport.airportdistanceservice.entity.AppUser;
import com.airport.airportdistanceservice.enums.Role;
import com.airport.airportdistanceservice.exception.InvalidTokenException;
import com.airport.airportdistanceservice.repository.UserRepository;
import com.airport.airportdistanceservice.security.JwtService;
import com.airport.airportdistanceservice.service.AuthenticationService;
import com.airport.airportdistanceservice.service.TokenBlacklistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final TokenBlacklistService tokenBlacklistService;

  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {

    if (userRepository.findByEmail(request.email()).isPresent()) {
              throw new IllegalArgumentException("Email is already registered: " + request.email());
    }

    AppUser user = new AppUser();
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(Role.USER);

    userRepository.save(user);

    UserDetails userDetails = buildUserDetails(user);

    return generateAuthResponse(userDetails);
  }

  @Override
  public AuthResponse authenticate(LoginRequest request) {
    Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    UserDetails userDetails = (UserDetails) auth.getPrincipal();

    return generateAuthResponse(userDetails);
  }

  @Override
  public AuthResponse refreshToken(RefreshTokenRequest request) {
    final String refreshToken = request.refreshToken();
    final String userEmail = jwtService.extractEmail(refreshToken);
    final String tokenType = jwtService.extractType(refreshToken);

    if (userEmail == null || !"REFRESH".equals(tokenType)) {
      throw new InvalidTokenException("Invalid refresh token");
    }

    AppUser user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    UserDetails userDetails = buildUserDetails(user);

    if (jwtService.isTokenValid(refreshToken, userDetails)) {
      String accessToken = jwtService.generateAccessToken(userDetails);
      return new AuthResponse(accessToken, refreshToken);
    }

    throw new InvalidTokenException("Refresh token expired");
  }

  @Override
  public void logout(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Invalid Authorization header");
    }
    String token = authHeader.substring(7);
    long remainingTime = jwtService.getRemainingExpireTime(token);
    tokenBlacklistService.addToBlacklist(token, remainingTime);
  }

  private UserDetails buildUserDetails(AppUser user) {
    return User.withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
  }

  private AuthResponse generateAuthResponse(UserDetails userDetails) {
    String accessToken = jwtService.generateAccessToken(userDetails);
    String refreshToken = jwtService.generateRefreshToken(userDetails);
    return new AuthResponse(accessToken, refreshToken);
  }
}