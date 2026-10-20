package com.voyage.backend.controller;

import com.voyage.backend.model.Trip;
import com.voyage.backend.service.TripService;
import com.voyage.backend.exception.TripNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // Create a new trip
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Trip createTrip(@Valid @RequestBody Trip trip) {
        return tripService.createTrip(trip);
    }

    // Get all trips
    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    // Get a trip by ID
    @GetMapping("/{id}")
    public Trip getTripById(@PathVariable Long id) {
        return tripService.getTripById(id)
            .orElseThrow(() ->
                    new TripNotFoundException(
                            "Trip not found with ID: " + id));
}

    // Update an existing trip
    @PutMapping("/{id}")
    public Trip updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody Trip trip) {
        return tripService.updateTrip(id, trip);
    }

    // Delete a trip
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
    }
}
