package com.airport.airportdistanceservice.dto.airport;

public record AirportInfo(
        String iataCode,
        String airportName,
        String city,
        String country
) {}
