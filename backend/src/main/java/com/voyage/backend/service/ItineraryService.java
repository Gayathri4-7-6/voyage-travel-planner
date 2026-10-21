
package com.voyage.backend.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    /**
     * Builds a date-wise schedule from the user's selected attractions.
     * Visit durations depend on attraction category, and the schedule
     * considers a lunch break and travel buffers. The resulting itinerary
     * is saved against the trip so it can be retrieved later.
     */
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

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        LocalDate date = startDate;
        int minutes = 9 * 60;

        for (int i = 0; i < attractions.size(); i++) {

            SelectedAttraction attraction = attractions.get(i);

            if (attraction == null || attraction.name() == null
                    || attraction.name().isBlank()) {
                throw new IllegalArgumentException(
                        "Invalid attraction.");
            }
            // Spread visits over the trip rather than filling the first day first.
            // Later moves visits forward if a day becomes too packed
            // Distribute attractions across trip days
            int dayIndex = (int) ((long) i * totalDays / attractions.size());
            LocalDate targetDate = startDate.plusDays(dayIndex);

            if (targetDate.isAfter(date)) {
                date = targetDate;
                minutes = 9 * 60;
            }

            int duration = getDuration(attraction.category());

            while (true) {

                // Lunch break: 1 PM to 2 PM
                if (minutes >= 13 * 60 && minutes < 14 * 60) {
                    minutes = 14 * 60;
                }
                // Ensure nothing scheduled during lunch,f a visit would cross it,
                // schedule that visit after lunch instead.
                // Do not schedule visits across lunch
                if (minutes < 13 * 60 &&
                        minutes + duration > 13 * 60) {
                    minutes = 14 * 60;
                }

                // Move to next day if visit won't fit
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

            // 30-minute travel buffer
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
