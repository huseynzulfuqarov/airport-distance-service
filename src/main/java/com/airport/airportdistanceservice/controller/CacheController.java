package com.airport.airportdistanceservice.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/cache")
// @PreAuthorize("hasRole('ADMIN')")
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