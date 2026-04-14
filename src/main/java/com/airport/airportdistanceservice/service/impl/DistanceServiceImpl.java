package com.airport.airportdistanceservice.service.impl;

import com.airport.airportdistanceservice.client.AirportGapAirportResponse;
import com.airport.airportdistanceservice.client.AirportGapClient;
import com.airport.airportdistanceservice.dto.airport.AirportInfo;
import com.airport.airportdistanceservice.dto.airport.DistanceRequest;
import com.airport.airportdistanceservice.dto.airport.DistanceResponse;
import com.airport.airportdistanceservice.service.DistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistanceServiceImpl implements DistanceService {

  private static final double EARTH_RADIUS_KM = 6371.0;
  private final AirportGapClient airportGapClient;

  @Override
  @Cacheable(value = "distances", key = "#request.origin() + '-' + #request.destination()")
  public DistanceResponse calculateDistance(DistanceRequest request) {
    String origin = request.origin();
    String destination = request.destination();

    AirportGapAirportResponse originResponse = airportGapClient.getAirportInfo(origin);
    AirportGapAirportResponse destinationResponse = airportGapClient.getAirportInfo(destination);

      double distance = haversine(
              Double.parseDouble(originResponse.data().attributes().latitude()),
              Double.parseDouble(originResponse.data().attributes().longitude()),
              Double.parseDouble(destinationResponse.data().attributes().latitude()),
              Double.parseDouble(destinationResponse.data().attributes().longitude())
      );

    AirportInfo originInfo =
        new AirportInfo(
            originResponse.data().attributes().iata(),
            originResponse.data().attributes().name(),
            originResponse.data().attributes().city(),
            originResponse.data().attributes().country());

    AirportInfo destinationInfo =
        new AirportInfo(
            destinationResponse.data().attributes().iata(),
            destinationResponse.data().attributes().name(),
            destinationResponse.data().attributes().city(),
            destinationResponse.data().attributes().country());

    return new DistanceResponse(originInfo , destinationInfo , distance);
  }

  private double haversine(double lat1, double lon1, double lat2, double lon2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return EARTH_RADIUS_KM * c;
  }
}
