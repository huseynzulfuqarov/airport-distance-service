package com.airport.airportdistanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AirportDistanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirportDistanceServiceApplication.class, args);
	}

}
