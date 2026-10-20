
package com.voyage.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voyage.backend.dto.SelectedAttraction;
import com.voyage.backend.model.Itinerary;
import com.voyage.backend.service.ItineraryService;

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
            @RequestBody List<SelectedAttraction> attractions) {

        return itineraryService.generateItinerary(tripId, attractions);
    }
    
@GetMapping
    public Itinerary getItinerary(@PathVariable Long tripId) {
        return itineraryService.getItinerary(tripId);
    }

}
