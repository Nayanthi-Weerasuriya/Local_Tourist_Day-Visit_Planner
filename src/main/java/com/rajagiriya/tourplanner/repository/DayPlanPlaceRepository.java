package com.rajagiriya.tourplanner.repository;

import com.rajagiriya.tourplanner.entity.DayPlanPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DayPlanPlaceRepository extends JpaRepository<DayPlanPlace, Long> {

    boolean existsByDayPlanIdAndPlaceId(Long dayPlanId, Long placeId);

    List<DayPlanPlace> findByDayPlanIdOrderByVisitOrderAsc(Long dayPlanId);

    Optional<DayPlanPlace> findByDayPlanIdAndPlaceId(Long dayPlanId, Long placeId);
}
