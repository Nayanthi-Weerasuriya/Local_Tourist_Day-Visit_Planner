package com.rajagiriya.tourplanner.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PlaceRequest {

    @NotBlank(message = "Name is required.")
    @Size(max = 120, message = "Name must be under 120 characters.")
    private String name;

    @NotBlank(message = "Category is required.")
    private String category;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotNull(message = "Distance is required.")
    @DecimalMin(value = "0.1", message = "Distance must be at least 0.1 km.")
    @DecimalMax(value = "25.0", message = "Only places within 25 km of Rajagiriya are allowed.")
    private Double distanceKm;

    @NotBlank(message = "Opening time is required.")
    private String openingTime;

    @NotBlank(message = "Closing time is required.")
    private String closingTime;

    @NotBlank(message = "Travel tips are required.")
    private String travelTips;

    @NotBlank(message = "Address is required.")
    private String address;

    @NotNull(message = "Latitude is required.")
    @DecimalMin(value = "-90.0", message = "Latitude must be valid.")
    @DecimalMax(value = "90.0", message = "Latitude must be valid.")
    private Double latitude;

    @NotNull(message = "Longitude is required.")
    @DecimalMin(value = "-180.0", message = "Longitude must be valid.")
    @DecimalMax(value = "180.0", message = "Longitude must be valid.")
    private Double longitude;

    @NotBlank(message = "Image URL is required.")
    private String imageUrl;

    private Boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getTravelTips() {
        return travelTips;
    }

    public void setTravelTips(String travelTips) {
        this.travelTips = travelTips;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
