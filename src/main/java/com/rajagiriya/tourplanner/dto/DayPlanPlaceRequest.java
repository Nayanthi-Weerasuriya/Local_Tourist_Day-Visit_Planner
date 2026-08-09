package com.rajagiriya.tourplanner.dto;

import jakarta.validation.constraints.NotNull;

public class DayPlanPlaceRequest {

    @NotNull(message = "Place ID is required.")
    private Long placeId;

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }
}
