package com.voyage.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voyage.backend.model.Itinerary;

public interface ItineraryRepository
        extends JpaRepository<Itinerary, Long> {

    Optional<Itinerary> findByTrip_Id(Long tripId);

    void deleteByTrip_Id(Long tripId);
}