package com.airport.airportdistanceservice.service;

import com.airport.airportdistanceservice.dto.DistanceRequest;
import com.airport.airportdistanceservice.dto.DistanceResponse;

public interface DistanceService {
    DistanceResponse calculateDistance(DistanceRequest request);
}
