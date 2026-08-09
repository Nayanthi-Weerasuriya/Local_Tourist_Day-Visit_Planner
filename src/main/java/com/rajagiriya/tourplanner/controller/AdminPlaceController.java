package com.rajagiriya.tourplanner.controller;

import com.rajagiriya.tourplanner.dto.ApiMessageResponse;
import com.rajagiriya.tourplanner.dto.PlaceRequest;
import com.rajagiriya.tourplanner.dto.PlaceResponse;
import com.rajagiriya.tourplanner.service.PlaceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/places")
public class AdminPlaceController {

    private final PlaceService placeService;

    public AdminPlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public List<PlaceResponse> getAllPlaces() {
        return placeService.getAllPlacesForAdmin();
    }

    @PostMapping
    public PlaceResponse createPlace(@Valid @RequestBody PlaceRequest request) {
        return placeService.createPlace(request);
    }

    @PutMapping("/{id}")
    public PlaceResponse updatePlace(@PathVariable Long id, @Valid @RequestBody PlaceRequest request) {
        return placeService.updatePlace(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiMessageResponse deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
        return new ApiMessageResponse("Place deleted successfully.");
    }
}
