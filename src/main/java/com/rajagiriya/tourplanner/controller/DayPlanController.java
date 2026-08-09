package com.rajagiriya.tourplanner.controller;

import com.rajagiriya.tourplanner.dto.DayPlanCreateRequest;
import com.rajagiriya.tourplanner.dto.DayPlanPlaceRequest;
import com.rajagiriya.tourplanner.dto.DayPlanResponse;
import com.rajagiriya.tourplanner.dto.ReorderPlanRequest;
import com.rajagiriya.tourplanner.service.DayPlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/day-plans")
public class DayPlanController {

    private final DayPlanService dayPlanService;

    public DayPlanController(DayPlanService dayPlanService) {
        this.dayPlanService = dayPlanService;
    }

    @PostMapping
    public DayPlanResponse createPlan(@RequestBody(required = false) DayPlanCreateRequest request) {
        return dayPlanService.createPlan(request);
    }

    @PostMapping("/{planId}/places")
    public DayPlanResponse addPlaceToPlan(@PathVariable Long planId, @Valid @RequestBody DayPlanPlaceRequest request) {
        return dayPlanService.addPlaceToPlan(planId, request);
    }

    @GetMapping("/{planId}")
    public DayPlanResponse getPlan(@PathVariable Long planId) {
        return dayPlanService.getPlan(planId);
    }

    @DeleteMapping("/{planId}/places/{placeId}")
    public DayPlanResponse removePlaceFromPlan(@PathVariable Long planId, @PathVariable Long placeId) {
        return dayPlanService.removePlaceFromPlan(planId, placeId);
    }

    @PutMapping("/{planId}/places/reorder")
    public DayPlanResponse reorderPlan(@PathVariable Long planId, @Valid @RequestBody ReorderPlanRequest request) {
        return dayPlanService.reorderPlan(planId, request);
    }

    @PostMapping("/{planId}/generate")
    public DayPlanResponse generateSuggestedPlan(@PathVariable Long planId) {
        return dayPlanService.generateSuggestedPlan(planId);
    }
}
