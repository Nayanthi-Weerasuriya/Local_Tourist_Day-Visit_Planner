package com.rajagiriya.tourplanner.repository;

import com.rajagiriya.tourplanner.entity.DayPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {
}
