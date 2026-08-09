package com.rajagiriya.tourplanner.controller;

import com.rajagiriya.tourplanner.dto.PlaceResponse;
import com.rajagiriya.tourplanner.service.PlaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public List<PlaceResponse> getAllPlaces() {
        return placeService.getAllActivePlaces();
    }

    @GetMapping("/{id}")
    public PlaceResponse getPlaceById(@PathVariable Long id) {
        return placeService.getActivePlaceById(id);
    }

    @GetMapping("/category/{category}")
    public List<PlaceResponse> getPlacesByCategory(@PathVariable String category) {
        return placeService.getPlacesByCategory(category);
    }

    @GetMapping("/search")
    public List<PlaceResponse> searchPlaces(@RequestParam(name = "keyword", required = false) String keyword) {
        return placeService.searchPlaces(keyword);
    }
}
