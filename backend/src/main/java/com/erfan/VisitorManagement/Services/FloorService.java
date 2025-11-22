package com.erfan.VisitorManagement.Services;

import com.erfan.VisitorManagement.Dtos.CreateFloorDto;
import com.erfan.VisitorManagement.Exception.BadRequestException;
import com.erfan.VisitorManagement.Exception.ResourceNotFoundException;
import com.erfan.VisitorManagement.Models.Building;
import com.erfan.VisitorManagement.Models.Floor;
import com.erfan.VisitorManagement.Repos.BuildingRepository;
import com.erfan.VisitorManagement.Repos.FloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FloorService {
    private final FloorRepository floorRepo;
    private final BuildingRepository buildingRepo;

    public Floor createFloor(CreateFloorDto dto) {
        if (!buildingRepo.existsById(dto.getBuildingId())) throw new ResourceNotFoundException("Building");
        if (floorRepo.existsByBuildingIdAndFloorNumber(dto.getBuildingId(), dto.getFloorNumber()))
            throw new BadRequestException("Floor already exists");

        Building b = buildingRepo.getReferenceById(dto.getBuildingId());
        Floor floor = Floor.builder().floorNumber(dto.getFloorNumber()).building(b).build();
        return floorRepo.save(floor);
    }

    public List<Floor> getFloorsForBuilding(Long buildingId) {
        return floorRepo.findByBuildingIdOrderByFloorNumberAsc(buildingId);
    }
}
