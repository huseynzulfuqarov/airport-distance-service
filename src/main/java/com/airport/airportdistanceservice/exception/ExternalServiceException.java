package com.airport.airportdistanceservice.exception;

public class ExternalServiceException extends BaseException {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
