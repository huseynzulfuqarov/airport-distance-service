package com.airport.airportdistanceservice.dto;

public record DistanceResponse(
        AirportInfo origin,
        AirportInfo destination,
        Double distanceInKm
) {}