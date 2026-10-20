
package com.voyage.backend.service;



import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.voyage.backend.exception.TripNotFoundException;
import com.voyage.backend.model.Itinerary;
import com.voyage.backend.model.ItineraryItem;
import com.voyage.backend.model.Trip;
import com.voyage.backend.repository.ItineraryRepository;
import com.voyage.backend.repository.TripRepository;

@Service
public class ItineraryService {

    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;

    public ItineraryService(
            TripRepository tripRepository,
            ItineraryRepository itineraryRepository) {
        this.tripRepository = tripRepository;
        this.itineraryRepository = itineraryRepository;
    }

    public Itinerary generateItinerary(
            Long tripId,
            List<String> attractions) {

            Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() ->
                    new TripNotFoundException(
                            "Trip not found with ID: " + tripId));

        LocalDate startDate = trip.getStartDate();
        LocalDate endDate = trip.getEndDate();

        if (startDate == null || endDate == null
                || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "The trip dates are invalid.");
        }

        if (attractions == null || attractions.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one attraction is required.");
        }

        Itinerary itinerary = itineraryRepository
        .findByTrip_Id(tripId)
        .orElseGet(Itinerary::new);
        itinerary.setTrip(trip);

        List<ItineraryItem> items = new ArrayList<>();

        long totalDays = ChronoUnit.DAYS.between(
                startDate, endDate) + 1;

        
        String[][] timeSlots = {
        {"09:00", "11:00"},
        {"11:30", "13:00"},
        {"14:00", "16:00"},
        {"16:30", "18:00"}
        };

        int[] counts = new int[(int) totalDays];

        for (int i = 0; i < attractions.size(); i++) {
        int dayIndex = (int) (i % totalDays);
        LocalDate date = startDate.plusDays(dayIndex);

        int slotIndex = counts[dayIndex];

        if (slotIndex >= timeSlots.length) {
                throw new IllegalArgumentException(
                "Too many attractions for the selected trip duration."
                );
        }

        ItineraryItem item = new ItineraryItem(
                date,
                attractions.get(i),
                "Visit " + attractions.get(i),
                timeSlots[slotIndex][0],
                timeSlots[slotIndex][1]
        );

        items.add(item);
        counts[dayIndex]++;
        }
        itinerary.setItems(items);
        return itineraryRepository.save(itinerary);
    }
    public Itinerary getItinerary(Long tripId) {
        return itineraryRepository.findByTrip_Id(tripId)
            .orElseThrow(() ->
                    new TripNotFoundException(
                            "Itinerary not found for trip ID: " + tripId));
    }
}
