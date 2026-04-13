package com.airport.airportdistanceservice.dto;

public record AirportInfo(
        String iataCode,
        String airportName,
        String city,
        String country
) {}
