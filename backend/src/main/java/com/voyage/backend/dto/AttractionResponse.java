package com.voyage.backend.dto;

public record AttractionResponse(String name, String description, Double latitude, Double longitude, String category, String imageUrl)
{}