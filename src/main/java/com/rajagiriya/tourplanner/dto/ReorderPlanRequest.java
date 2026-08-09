package com.rajagiriya.tourplanner.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ReorderPlanRequest {

    @NotEmpty(message = "Place order is required.")
    private List<Long> placeIds;

    public List<Long> getPlaceIds() {
        return placeIds;
    }

    public void setPlaceIds(List<Long> placeIds) {
        this.placeIds = placeIds;
    }
}
