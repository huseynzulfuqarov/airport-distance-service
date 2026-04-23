package com.airport.airportdistanceservice.service.impl;

import com.airport.airportdistanceservice.client.AirportGapAirportResponse;
import com.airport.airportdistanceservice.client.AirportGapClient;
import com.airport.airportdistanceservice.dto.airport.DistanceRequest;
import com.airport.airportdistanceservice.dto.airport.DistanceResponse;
import com.airport.airportdistanceservice.exception.AirportNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistanceServiceImplTest {

    @Mock
    private AirportGapClient airportGapClient;

    @InjectMocks
    private DistanceServiceImpl distanceServiceImpl;

    private AirportGapAirportResponse gydResponse;
    private AirportGapAirportResponse istResponse;

    @BeforeEach
    void setUp() {
        gydResponse = new AirportGapAirportResponse(
                new AirportGapAirportResponse.Data(
                        new AirportGapAirportResponse.Attributes(
                                "Heydar Aliyev International Airport",
                                "Baku",
                                "Azerbaijan",
                                "GYD",
                                "40.4675",
                                "50.0469"
                        )
                )
        );
        istResponse = new AirportGapAirportResponse(
                new AirportGapAirportResponse.Data(
                        new AirportGapAirportResponse.Attributes(
                                "Istanbul Airport",
                                "Istanbul",
                                "Turkey",
                                "IST",
                                "40.9767",
                                "28.8147"
                        )
                )
        );
    }

    @Test
    void calculateDistance() {

        when(airportGapClient.getAirportInfo("GYD")).thenReturn(gydResponse);
        when(airportGapClient.getAirportInfo("IST")).thenReturn(istResponse);

        DistanceRequest request = new DistanceRequest("GYD", "IST");

        DistanceResponse response = distanceServiceImpl.calculateDistance(request);

        assertThat(response).isNotNull();
        assertThat(response.origin().iataCode()).isEqualTo("GYD");
        assertThat(response.destination().iataCode()).isEqualTo("IST");
        assertThat(response.distanceInKm()).isGreaterThan(0);
        assertThat(response.distanceInKm()).isBetween(1500.0, 2000.0);

        verify(airportGapClient).getAirportInfo("GYD");
        verify(airportGapClient).getAirportInfo("IST");
        verify(airportGapClient, times(2)).getAirportInfo(anyString());
    }

    @Test
    void shouldThrowWhenSameAirport() {

        DistanceRequest request = new DistanceRequest("GYD", "GYD");

        assertThatThrownBy(() -> distanceServiceImpl.calculateDistance(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Origin and destination airports must be different");

    }

    @Test
    void shouldThrowWhenAirportNotFound() {
        when(airportGapClient.getAirportInfo("GYD")).thenReturn(gydResponse);
        when(airportGapClient.getAirportInfo("XXX")).thenThrow(new AirportNotFoundException("Airport not found"));

        DistanceRequest request = new DistanceRequest("GYD", "XXX");
        assertThatThrownBy(() -> distanceServiceImpl.calculateDistance(request))
                .isInstanceOf(AirportNotFoundException.class)
                .hasMessage("Airport not found");
    }
}