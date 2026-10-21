package com.voyage.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.voyage.backend.exception.TripNotFoundException;
import com.voyage.backend.model.Trip;
import com.voyage.backend.service.TripService;

import jakarta.validation.Valid;

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
