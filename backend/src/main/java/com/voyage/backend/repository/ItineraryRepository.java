
package com.voyage.backend.repository;

import com.voyage.backend.model.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItineraryRepository
        extends JpaRepository<Itinerary, Long> {

    Optional<Itinerary> findByTrip_Id(Long tripId);
}
