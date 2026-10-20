
package com.voyage.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.voyage.backend.dto.SelectedAttraction;
import com.voyage.backend.model.Trip;

@Service
public class BudgetService {

    public BudgetBreakdown calculateBudget(
            Trip trip,
            List<SelectedAttraction> attractions) {

        if (trip == null || trip.getBudget() == null) {
            throw new IllegalArgumentException(
                    "Trip budget is required.");
        }

        if (attractions == null || attractions.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one attraction is required.");
        }

        BigDecimal attractionTotal = BigDecimal.ZERO;

        for (SelectedAttraction attraction : attractions) {

            if (attraction == null) {
                throw new IllegalArgumentException(
                        "Invalid attraction.");
            }

            attractionTotal = attractionTotal.add(
                    getEstimatedCost(attraction.category()));
        }

        BigDecimal totalBudget = trip.getBudget();
        BigDecimal hotelBudget =
                totalBudget.subtract(attractionTotal);

        if (hotelBudget.compareTo(BigDecimal.ZERO) < 0) {
            hotelBudget = BigDecimal.ZERO;
        }

        return new BudgetBreakdown(
                totalBudget,
                attractionTotal,
                hotelBudget
        );
    }

    private BigDecimal getEstimatedCost(String category) {

        if (category == null) {
            return BigDecimal.valueOf(150);
        }

        return switch (category.toLowerCase()) {
            case "nature", "beach" ->
                    BigDecimal.valueOf(200);
            case "religious", "temple" ->
                    BigDecimal.valueOf(100);
            case "mountain", "viewpoint" ->
                    BigDecimal.valueOf(250);
            case "museum", "historical" ->
                    BigDecimal.valueOf(200);
            case "wildlife", "adventure" ->
                    BigDecimal.valueOf(350);
            case "park" ->
                    BigDecimal.valueOf(100);
            default ->
                    BigDecimal.valueOf(150);
        };
    }

    public record BudgetBreakdown(
            BigDecimal totalBudget,
            BigDecimal attractionCost,
            BigDecimal hotelBudget) {
    }
}
