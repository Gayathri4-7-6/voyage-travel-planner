
package com.voyage.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voyage.backend.dto.SelectedAttraction;
import com.voyage.backend.exception.TripNotFoundException;
import com.voyage.backend.model.Trip;
import com.voyage.backend.repository.TripRepository;
import com.voyage.backend.service.BudgetService;
import com.voyage.backend.service.BudgetService.BudgetBreakdown;

@RestController
@RequestMapping("/api/trips/{tripId}/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final TripRepository tripRepository;

    public BudgetController(
            BudgetService budgetService,
            TripRepository tripRepository) {
        this.budgetService = budgetService;
        this.tripRepository = tripRepository;
    }

    @PostMapping("/calculate")
    public BudgetBreakdown calculateBudget(
            @PathVariable Long tripId,
            @RequestBody List<SelectedAttraction> attractions) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() ->
                        new TripNotFoundException(
                                "Trip not found with ID: " + tripId));

        return budgetService.calculateBudget(trip, attractions);
    }
}
