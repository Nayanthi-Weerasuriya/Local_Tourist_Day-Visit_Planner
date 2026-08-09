package com.rajagiriya.tourplanner.service;

import com.rajagiriya.tourplanner.dto.DayPlanCreateRequest;
import com.rajagiriya.tourplanner.dto.DayPlanPlaceRequest;
import com.rajagiriya.tourplanner.dto.DayPlanResponse;
import com.rajagiriya.tourplanner.dto.ReorderPlanRequest;

public interface DayPlanService {

    DayPlanResponse createPlan(DayPlanCreateRequest request);

    DayPlanResponse addPlaceToPlan(Long planId, DayPlanPlaceRequest request);

    DayPlanResponse getPlan(Long planId);

    DayPlanResponse removePlaceFromPlan(Long planId, Long placeId);

    DayPlanResponse reorderPlan(Long planId, ReorderPlanRequest request);

    DayPlanResponse generateSuggestedPlan(Long planId);
}
