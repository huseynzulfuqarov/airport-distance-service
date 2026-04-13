package com.airport.airportdistanceservice.controller;

import com.airport.airportdistanceservice.dto.DistanceRequest;
import com.airport.airportdistanceservice.dto.DistanceResponse;
import com.airport.airportdistanceservice.service.DistanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/distances")
@RequiredArgsConstructor
public class DistanceController {
    private final DistanceService distanceService;

    @PostMapping
    public ResponseEntity<DistanceResponse> calculateDistance(@Valid @RequestBody DistanceRequest request) {
        return ResponseEntity.ok(distanceService.calculateDistance(request));
    }
}
