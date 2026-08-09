package com.rajagiriya.tourplanner.service.impl;

import com.rajagiriya.tourplanner.dto.PlaceRequest;
import com.rajagiriya.tourplanner.dto.PlaceResponse;
import com.rajagiriya.tourplanner.entity.Place;
import com.rajagiriya.tourplanner.entity.PlaceCategory;
import com.rajagiriya.tourplanner.exception.BadRequestException;
import com.rajagiriya.tourplanner.exception.ResourceNotFoundException;
import com.rajagiriya.tourplanner.repository.PlaceRepository;
import com.rajagiriya.tourplanner.service.PlaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Transactional
public class PlaceServiceImpl implements PlaceService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final PlaceRepository placeRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceResponse> getAllActivePlaces() {
        return placeRepository.findByActiveTrueOrderByDistanceKmAscNameAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceResponse> getAllPlacesForAdmin() {
        return placeRepository.findAllByOrderByDistanceKmAscNameAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlaceResponse getActivePlaceById(Long id) {
        Place place = placeRepository.findById(id)
                .filter(Place::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found for ID " + id));
        return mapToResponse(place);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceResponse> getPlacesByCategory(String category) {
        PlaceCategory parsedCategory = parseCategory(category);
        return placeRepository.findByActiveTrueAndCategoryOrderByDistanceKmAscNameAsc(parsedCategory)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceResponse> searchPlaces(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllActivePlaces();
        }

        return placeRepository.searchActivePlaces(keyword.trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PlaceResponse createPlace(PlaceRequest request) {
        Place place = new Place();
        applyRequest(place, request);
        return mapToResponse(placeRepository.save(place));
    }

    @Override
    public PlaceResponse updatePlace(Long id, PlaceRequest request) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found for ID " + id));
        applyRequest(place, request);
        return mapToResponse(placeRepository.save(place));
    }

    @Override
    public void deletePlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found for ID " + id));
        placeRepository.delete(place);
    }

    private void applyRequest(Place place, PlaceRequest request) {
        LocalTime openingTime = parseTime(request.getOpeningTime(), "opening time");
        LocalTime closingTime = parseTime(request.getClosingTime(), "closing time");

        if (!closingTime.isAfter(openingTime)) {
            throw new BadRequestException("Closing time must be later than opening time.");
        }

        place.setName(request.getName().trim());
        place.setCategory(parseCategory(request.getCategory()));
        place.setDescription(request.getDescription().trim());
        place.setDistanceKm(request.getDistanceKm());
        place.setOpeningTime(openingTime);
        place.setClosingTime(closingTime);
        place.setTravelTips(request.getTravelTips().trim());
        place.setAddress(request.getAddress().trim());
        place.setLatitude(request.getLatitude());
        place.setLongitude(request.getLongitude());
        place.setImageUrl(request.getImageUrl().trim());
        place.setActive(request.getActive() == null || request.getActive());
    }

    private PlaceCategory parseCategory(String category) {
        try {
            return PlaceCategory.fromValue(category);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }

    private LocalTime parseTime(String value, String label) {
        try {
            return LocalTime.parse(value, TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new BadRequestException("Invalid " + label + ". Use HH:mm format.");
        }
    }

    private PlaceResponse mapToResponse(Place place) {
        PlaceResponse response = new PlaceResponse();
        response.setId(place.getId());
        response.setName(place.getName());
        response.setCategory(place.getCategory().getDisplayName());
        response.setDescription(place.getDescription());
        response.setDistanceKm(place.getDistanceKm());
        response.setOpeningTime(place.getOpeningTime().format(TIME_FORMATTER));
        response.setClosingTime(place.getClosingTime().format(TIME_FORMATTER));
        response.setTravelTips(place.getTravelTips());
        response.setAddress(place.getAddress());
        response.setLatitude(place.getLatitude());
        response.setLongitude(place.getLongitude());
        response.setImageUrl(place.getImageUrl());
        response.setActive(place.isActive());
        return response;
    }
}
