package com.rajagiriya.tourplanner.service;

import com.rajagiriya.tourplanner.dto.PlaceRequest;
import com.rajagiriya.tourplanner.dto.PlaceResponse;

import java.util.List;

public interface PlaceService {

    List<PlaceResponse> getAllActivePlaces();

    List<PlaceResponse> getAllPlacesForAdmin();

    PlaceResponse getActivePlaceById(Long id);

    List<PlaceResponse> getPlacesByCategory(String category);

    List<PlaceResponse> searchPlaces(String keyword);

    PlaceResponse createPlace(PlaceRequest request);

    PlaceResponse updatePlace(Long id, PlaceRequest request);

    void deletePlace(Long id);
}
