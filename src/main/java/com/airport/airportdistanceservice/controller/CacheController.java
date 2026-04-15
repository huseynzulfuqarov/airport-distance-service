package com.airport.airportdistanceservice.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/cache")
@PreAuthorize("hasRole('ADMIN')")
public class CacheController {

    @DeleteMapping("/airports/{iataCode}")
    @CacheEvict(value = "airports", key = "#iataCode")
    public String clearAirportCache(@PathVariable String iataCode) {
        return iataCode + " aeroportunun keşi təmizləndi.";
    }

    @DeleteMapping("/distances")
    @CacheEvict(value = "distances", allEntries = true)
    public String clearAllDistancesCache() {
        return "Bütün hesablanmış məsafə keşləri təmizləndi.";
    }
}