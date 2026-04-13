package com.airport.airportdistanceservice.client;

public record AirportGapAirportResponse(Data data) {
    public record Data(Attributes attributes) {}

    public record Attributes(
            String name,
            String city,
            String country,
            String iata,
            String latitude,
            String longitude
    ){}
}
