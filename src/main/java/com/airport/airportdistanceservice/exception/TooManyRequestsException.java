package com.airport.airportdistanceservice.exception;

public class TooManyRequestsException extends BaseException {
    public TooManyRequestsException(String message) {
        super(message);
    }

    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }
}
