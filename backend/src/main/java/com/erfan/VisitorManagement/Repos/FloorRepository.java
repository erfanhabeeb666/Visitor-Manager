package com.erfan.VisitorManagement.Repos;

import com.erfan.VisitorManagement.Models.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findByBuildingIdOrderByFloorNumberAsc(Long buildingId);
    boolean existsByBuildingIdAndFloorNumber(Long buildingId, Integer floorNumber);
}
