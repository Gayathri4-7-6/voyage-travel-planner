package com.voyage.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
@RestControllerAdvice
/**
 * Converts application and validation exceptions into consistent
 * HTTP error responses instead of exposing server failures.
 */
public class GlobalExceptionHandler {

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<String> handleTripNotFound(
            TripNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidRequest(
            IllegalArgumentException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
    
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<String> handleValidationErrors(
                MethodArgumentNotValidException exception) {

        String fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .collect(java.util.stream.Collectors.joining(", "));

        String objectErrors = exception.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .collect(java.util.stream.Collectors.joining(", "));

        String errors = java.util.stream.Stream.of(fieldErrors, objectErrors)
                .filter(message -> !message.isBlank())
                .collect(java.util.stream.Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors.isBlank() ? "Invalid request." : errors);
        }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<String> handleMethodValidation(
            HandlerMethodValidationException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid request: please check the supplied values.");
    }
}
