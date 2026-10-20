
package com.voyage.backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.voyage.backend.dto.SelectedAttraction;
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
            List<SelectedAttraction> attractions) {

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

        LocalDate date = startDate;
        int minutes = 9 * 60;

        for (SelectedAttraction attraction : attractions) {

            if (attraction == null || attraction.name() == null
                    || attraction.name().isBlank()) {
                throw new IllegalArgumentException(
                        "Invalid attraction.");
            }

            int duration = getDuration(attraction.category());

            // Move to the next day if the visit cannot fit
            // before lunch or before 6 PM
            while (true) {

                // Lunch break: 1 PM to 2 PM
                if (minutes >= 13 * 60 &&
                        minutes < 14 * 60) {
                    minutes = 14 * 60;
                }

                // Avoid scheduling a visit across lunch
                if (minutes < 13 * 60 &&
                        minutes + duration > 13 * 60) {
                    minutes = 14 * 60;
                }

                // Move to next day if there is no time
                if (minutes + duration > 18 * 60) {
                    date = date.plusDays(1);
                    minutes = 9 * 60;
                    continue;
                }

                break;
            }

            if (date.isAfter(endDate)) {
                throw new IllegalArgumentException(
                        "Too many attractions for the selected dates.");
            }

            int endMinutes = minutes + duration;

            String startTime = String.format(
                    "%02d:%02d",
                    minutes / 60, minutes % 60);

            String endTime = String.format(
                    "%02d:%02d",
                    endMinutes / 60, endMinutes % 60);

            ItineraryItem item = new ItineraryItem(
                    date,
                    attraction.name(),
                    "Visit " + attraction.name(),
                    startTime,
                    endTime
            );

            items.add(item);

            // 30-minute gap between attractions
            minutes = endMinutes + 30;
        }

        itinerary.setItems(items);

        return itineraryRepository.save(itinerary);
    }

    private int getDuration(String category) {

        if (category == null) {
            return 90;
        }

        return switch (category) {
            case "Museum" -> 120;
            case "Religious" -> 90;
            case "Historical" -> 120;
            case "Wildlife" -> 240;
            case "Nature" -> 120;
            case "Viewpoint" -> 60;
            case "Park" -> 90;
            case "Adventure" -> 240;
            default -> 90;
        };
    }

    public Itinerary getItinerary(Long tripId) {

        return itineraryRepository.findByTrip_Id(tripId)
                .orElseThrow(() ->
                        new TripNotFoundException(
                                "Itinerary not found for trip ID: " + tripId));
    }
}
