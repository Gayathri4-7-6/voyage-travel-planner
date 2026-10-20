
package com.voyage.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voyage.backend.exception.TripNotFoundException;
import com.voyage.backend.model.Trip;
import com.voyage.backend.repository.ItineraryRepository;
import com.voyage.backend.repository.TripRepository;
@Service
public class TripService {

    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;
    public TripService(TripRepository tripRepository,ItineraryRepository itineraryRepository) {
        this.tripRepository = tripRepository;
        this.itineraryRepository = itineraryRepository;
    }

    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findById(id);
    }

    public Trip updateTrip(Long id, Trip updatedTrip) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() ->
                        new TripNotFoundException("Trip not found with ID: " + id));

        existingTrip.setTitle(updatedTrip.getTitle());
        existingTrip.setDestination(updatedTrip.getDestination());
        existingTrip.setStartDate(updatedTrip.getStartDate());
        existingTrip.setEndDate(updatedTrip.getEndDate());
        existingTrip.setBudget(updatedTrip.getBudget());
        existingTrip.setDescription(updatedTrip.getDescription());

        return tripRepository.save(existingTrip);
    }

    @Transactional
    public void deleteTrip(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new TripNotFoundException(
                    "Trip not found with ID: " + id);
        }

        itineraryRepository.deleteByTrip_Id(id);
        tripRepository.deleteById(id);
    }
}
