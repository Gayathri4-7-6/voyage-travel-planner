
package com.voyage.backend.model;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class ItineraryItem {

    private LocalDate date;
    private String attractionName;
    private String description;
    private String startTime;
    private String endTime;

    public ItineraryItem() {
    }

    public ItineraryItem(
            LocalDate date,
            String attractionName,
            String description,
            String startTime,
            String endTime) {
        this.date = date;
        this.attractionName = attractionName;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
