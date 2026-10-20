
package com.voyage.backend.controller;

import com.voyage.backend.model.Itinerary;
import com.voyage.backend.service.ItineraryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/itinerary")
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @PostMapping("/generate")
    public Itinerary generateItinerary(
            @PathVariable Long tripId,
            @RequestBody List<String> attractions) {

        return itineraryService.generateItinerary(
                tripId, attractions);
    }
    
@GetMapping
    public Itinerary getItinerary(@PathVariable Long tripId) {
        return itineraryService.getItinerary(tripId);
    }

}
