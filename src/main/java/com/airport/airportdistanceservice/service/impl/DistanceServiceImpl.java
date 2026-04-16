package com.airport.airportdistanceservice.service.impl;

import com.airport.airportdistanceservice.client.AirportGapAirportResponse;
import com.airport.airportdistanceservice.client.AirportGapClient;
import com.airport.airportdistanceservice.dto.airport.AirportInfo;
import com.airport.airportdistanceservice.dto.airport.DistanceRequest;
import com.airport.airportdistanceservice.dto.airport.DistanceResponse;
import com.airport.airportdistanceservice.exception.ExternalServiceException;
import com.airport.airportdistanceservice.service.DistanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistanceServiceImpl implements DistanceService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private final AirportGapClient airportGapClient;

    @Override
    @Cacheable(value = "distances",
            key = "T(java.util.stream.Stream).of(#request.origin(), #request.destination()).sorted().toList().toString()", sync = true)
    public DistanceResponse calculateDistance(DistanceRequest request) {

        log.info("Calculating distance for request: {}", request);

        String origin = request.origin();
        String destination = request.destination();

        log.info("Calculating distance: {} → {}", origin, destination);

        if (origin.equals(destination)) {
            log.warn("Origin and destination are the same: {}", origin);
            throw new IllegalArgumentException("Origin and destination airports must be different");
        }

        AirportGapAirportResponse originResponse = airportGapClient.getAirportInfo(origin);
        AirportGapAirportResponse destinationResponse = airportGapClient.getAirportInfo(destination);

        log.info("AirportGapAirportResponse: {}", originResponse);
        log.info("AirportGapAirportResponse: {}", destinationResponse);

        double lat1 = parseCoordinate(originResponse.data().attributes().latitude(), origin);
        double lon1 = parseCoordinate(originResponse.data().attributes().longitude(), origin);
        double lat2 = parseCoordinate(destinationResponse.data().attributes().latitude(), destination);
        double lon2 = parseCoordinate(destinationResponse.data().attributes().longitude(), destination);

        log.info("Parsed coordinates: {} ({}), {} ({})", lat1, lon1, lat2, lon2);

        double distance = haversine(lat1, lon1, lat2, lon2);

        log.info("Distance: {}", distance);

        return new DistanceResponse(buildInfo(originResponse), buildInfo(destinationResponse), distance);
    }

    private double parseCoordinate(String value, String iataCode) {
        try {
            return Double.parseDouble(value);
        } catch (NullPointerException | NumberFormatException e) {
            log.warn("Invalid coordinate {}: {}", iataCode, value);
            throw new ExternalServiceException("Coordinate is missing or invalid for airport: " + iataCode);
        }
    }

    private AirportInfo buildInfo(AirportGapAirportResponse response) {
        var attr = response.data().attributes();
        return new AirportInfo(attr.iata(), attr.name(), attr.city(), attr.country());
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round((EARTH_RADIUS_KM * c) * 100.0) / 100.0;
    }
}
