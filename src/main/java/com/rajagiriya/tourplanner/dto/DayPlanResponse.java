package com.rajagiriya.tourplanner.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DayPlanResponse {

    private Long id;
    private String plannerCode;
    private LocalDateTime createdAt;
    private int totalPlaces;
    private List<DayPlanPlaceResponse> places;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlannerCode() {
        return plannerCode;
    }

    public void setPlannerCode(String plannerCode) {
        this.plannerCode = plannerCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalPlaces() {
        return totalPlaces;
    }

    public void setTotalPlaces(int totalPlaces) {
        this.totalPlaces = totalPlaces;
    }

    public List<DayPlanPlaceResponse> getPlaces() {
        return places;
    }

    public void setPlaces(List<DayPlanPlaceResponse> places) {
        this.places = places;
    }
}
