package com.rajagiriya.tourplanner.repository;

import com.rajagiriya.tourplanner.entity.Place;
import com.rajagiriya.tourplanner.entity.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByActiveTrueOrderByDistanceKmAscNameAsc();

    List<Place> findAllByOrderByDistanceKmAscNameAsc();

    List<Place> findByActiveTrueAndCategoryOrderByDistanceKmAscNameAsc(PlaceCategory category);

    @Query("""
            select p from Place p
            where p.active = true
            and (
                lower(p.name) like lower(concat('%', :keyword, '%'))
                or lower(p.description) like lower(concat('%', :keyword, '%'))
                or lower(p.address) like lower(concat('%', :keyword, '%'))
            )
            order by p.distanceKm asc, p.name asc
            """)
    List<Place> searchActivePlaces(@Param("keyword") String keyword);
}
