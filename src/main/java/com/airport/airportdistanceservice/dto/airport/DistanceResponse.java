package com.airport.airportdistanceservice.dto.airport;

public record DistanceResponse(
        AirportInfo origin,
        AirportInfo destination,
        Double distanceInKm
) {}