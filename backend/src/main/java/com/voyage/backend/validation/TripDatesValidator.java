package com.voyage.backend.validation;

import com.voyage.backend.model.Trip;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TripDatesValidator
        implements ConstraintValidator<ValidTripDates, Trip> {

    @Override
    public boolean isValid(
            Trip trip,
            ConstraintValidatorContext context) {

        if (trip == null
                || trip.getStartDate() == null
                || trip.getEndDate() == null) {
            return true;
        }

        return !trip.getEndDate().isBefore(trip.getStartDate());
    }
}
