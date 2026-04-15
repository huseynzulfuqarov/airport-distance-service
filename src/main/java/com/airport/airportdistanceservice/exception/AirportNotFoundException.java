package com.airport.airportdistanceservice.exception;

public class AirportNotFoundException extends BaseException {
    public AirportNotFoundException(String message) {
        super(message);
    }

    public AirportNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
