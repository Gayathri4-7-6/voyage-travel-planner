package com.voyage.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voyage.backend.dto.DestinationResponse;
import com.voyage.backend.service.DestinationService;


@RestController
@RequestMapping("/api/destinations")
public class DestinationController {
    private final DestinationService destinationService;
    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }
    @GetMapping("/search")
    public DestinationResponse searchDestination(@RequestParam String query){
        return destinationService.searchDestination(query);
    }

}