package com.rajagiriya.tourplanner.service.impl;

import com.rajagiriya.tourplanner.dto.DayPlanCreateRequest;
import com.rajagiriya.tourplanner.dto.DayPlanPlaceRequest;
import com.rajagiriya.tourplanner.dto.DayPlanPlaceResponse;
import com.rajagiriya.tourplanner.dto.DayPlanResponse;
import com.rajagiriya.tourplanner.dto.ReorderPlanRequest;
import com.rajagiriya.tourplanner.entity.DayPlan;
import com.rajagiriya.tourplanner.entity.DayPlanPlace;
import com.rajagiriya.tourplanner.entity.Place;
import com.rajagiriya.tourplanner.exception.BadRequestException;
import com.rajagiriya.tourplanner.exception.ResourceNotFoundException;
import com.rajagiriya.tourplanner.repository.DayPlanPlaceRepository;
import com.rajagiriya.tourplanner.repository.DayPlanRepository;
import com.rajagiriya.tourplanner.repository.PlaceRepository;
import com.rajagiriya.tourplanner.service.DayPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DayPlanServiceImpl implements DayPlanService {

    private final DayPlanRepository dayPlanRepository;
    private final DayPlanPlaceRepository dayPlanPlaceRepository;
    private final PlaceRepository placeRepository;

    public DayPlanServiceImpl(
            DayPlanRepository dayPlanRepository,
            DayPlanPlaceRepository dayPlanPlaceRepository,
            PlaceRepository placeRepository
    ) {
        this.dayPlanRepository = dayPlanRepository;
        this.dayPlanPlaceRepository = dayPlanPlaceRepository;
        this.placeRepository = placeRepository;
    }

    @Override
    public DayPlanResponse createPlan(DayPlanCreateRequest request) {
        DayPlan dayPlan = new DayPlan();
        String plannerCode = request != null && request.getPlannerCode() != null && !request.getPlannerCode().isBlank()
                ? request.getPlannerCode().trim()
                : "PLAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        dayPlan.setPlannerCode(plannerCode);
        return mapToResponse(dayPlanRepository.save(dayPlan));
    }

    @Override
    public DayPlanResponse addPlaceToPlan(Long planId, DayPlanPlaceRequest request) {
        DayPlan dayPlan = getPlanEntity(planId);
        Place place = placeRepository.findById(request.getPlaceId())
                .filter(Place::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found for ID " + request.getPlaceId()));

        if (dayPlanPlaceRepository.existsByDayPlanIdAndPlaceId(planId, place.getId())) {
            throw new BadRequestException("This place is already in the selected day plan.");
        }

        DayPlanPlace dayPlanPlace = new DayPlanPlace();
        dayPlanPlace.setDayPlan(dayPlan);
        dayPlanPlace.setPlace(place);
        dayPlanPlace.setVisitOrder(dayPlanPlaceRepository.findByDayPlanIdOrderByVisitOrderAsc(planId).size() + 1);
        dayPlanPlaceRepository.save(dayPlanPlace);

        return getPlan(planId);
    }

    @Override
    @Transactional(readOnly = true)
    public DayPlanResponse getPlan(Long planId) {
        DayPlan dayPlan = getPlanEntity(planId);
        return mapToResponse(dayPlan);
    }

    @Override
    public DayPlanResponse removePlaceFromPlan(Long planId, Long placeId) {
        DayPlanPlace dayPlanPlace = dayPlanPlaceRepository.findByDayPlanIdAndPlaceId(planId, placeId)
                .orElseThrow(() -> new ResourceNotFoundException("Selected place is not part of this plan."));

        dayPlanPlaceRepository.delete(dayPlanPlace);
        normalizeVisitOrder(planId);
        return getPlan(planId);
    }

    @Override
    public DayPlanResponse reorderPlan(Long planId, ReorderPlanRequest request) {
        getPlanEntity(planId);
        List<DayPlanPlace> currentPlaces = dayPlanPlaceRepository.findByDayPlanIdOrderByVisitOrderAsc(planId);

        if (currentPlaces.size() != request.getPlaceIds().size()) {
            throw new BadRequestException("Reorder request does not match the selected plan size.");
        }

        for (int index = 0; index < request.getPlaceIds().size(); index++) {
            Long placeId = request.getPlaceIds().get(index);
            DayPlanPlace matchingPlace = currentPlaces.stream()
                    .filter(item -> item.getPlace().getId().equals(placeId))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Invalid place order submitted."));
            matchingPlace.setVisitOrder(index + 1);
        }

        return getPlan(planId);
    }

    @Override
    public DayPlanResponse generateSuggestedPlan(Long planId) {
        getPlanEntity(planId);
        List<DayPlanPlace> sortedPlaces = dayPlanPlaceRepository.findByDayPlanIdOrderByVisitOrderAsc(planId)
                .stream()
                .sorted(Comparator.comparing(item -> item.getPlace().getDistanceKm()))
                .toList();

        for (int index = 0; index < sortedPlaces.size(); index++) {
            sortedPlaces.get(index).setVisitOrder(index + 1);
        }

        return getPlan(planId);
    }

    private DayPlan getPlanEntity(Long planId) {
        return dayPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Day plan not found for ID " + planId));
    }

    private void normalizeVisitOrder(Long planId) {
        List<DayPlanPlace> items = dayPlanPlaceRepository.findByDayPlanIdOrderByVisitOrderAsc(planId);
        for (int index = 0; index < items.size(); index++) {
            items.get(index).setVisitOrder(index + 1);
        }
    }

    private DayPlanResponse mapToResponse(DayPlan dayPlan) {
        List<DayPlanPlaceResponse> placeResponses = dayPlanPlaceRepository.findByDayPlanIdOrderByVisitOrderAsc(dayPlan.getId())
                .stream()
                .map(this::mapPlanPlace)
                .toList();

        DayPlanResponse response = new DayPlanResponse();
        response.setId(dayPlan.getId());
        response.setPlannerCode(dayPlan.getPlannerCode());
        response.setCreatedAt(dayPlan.getCreatedAt());
        response.setTotalPlaces(placeResponses.size());
        response.setPlaces(placeResponses);
        return response;
    }

    private DayPlanPlaceResponse mapPlanPlace(DayPlanPlace dayPlanPlace) {
        DayPlanPlaceResponse response = new DayPlanPlaceResponse();
        response.setPlaceId(dayPlanPlace.getPlace().getId());
        response.setName(dayPlanPlace.getPlace().getName());
        response.setCategory(dayPlanPlace.getPlace().getCategory().getDisplayName());
        response.setDistanceKm(dayPlanPlace.getPlace().getDistanceKm());
        response.setImageUrl(dayPlanPlace.getPlace().getImageUrl());
        response.setAddress(dayPlanPlace.getPlace().getAddress());
        response.setVisitOrder(dayPlanPlace.getVisitOrder());
        return response;
    }
}
