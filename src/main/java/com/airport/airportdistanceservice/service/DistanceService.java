package com.airport.airportdistanceservice.service;

import com.airport.airportdistanceservice.dto.airport.DistanceRequest;
import com.airport.airportdistanceservice.dto.airport.DistanceResponse;

public interface DistanceService {
    DistanceResponse calculateDistance(DistanceRequest request);
}
