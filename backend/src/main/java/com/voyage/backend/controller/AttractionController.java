package com.voyage.backend.controller;

import com.voyage.backend.dto.AttractionResponse;
import com.voyage.backend.service.AttractionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/destinations")
public class AttractionController {

    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    @GetMapping("/attractions")
    public List<AttractionResponse> getAttractions(
            @RequestParam String query) {

        return attractionService.getAttractions(query);
    }
}