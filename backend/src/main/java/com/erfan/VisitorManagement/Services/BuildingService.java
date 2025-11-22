package com.erfan.VisitorManagement.Services;

import com.erfan.VisitorManagement.Dtos.CreateBuildingDto;
import com.erfan.VisitorManagement.Exception.ResourceNotFoundException;
import com.erfan.VisitorManagement.Models.Building;
import com.erfan.VisitorManagement.Repos.BuildingRepository;
import com.erfan.VisitorManagement.Repos.FloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuildingService {
    private final BuildingRepository buildingRepo;
    private final FloorRepository floorRepo;

    public Building createBuilding(CreateBuildingDto dto) {
        Building b = Building.builder().name(dto.getName()).address(dto.getAddress()).build();
        return buildingRepo.save(b);
    }

    public Building updateBuilding(Long id, CreateBuildingDto dto) {
        Building b = buildingRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Building"));
        b.setName(dto.getName());
        b.setAddress(dto.getAddress());
        return buildingRepo.save(b);
    }

    public void deleteBuilding(Long id) {
        buildingRepo.deleteById(id); // cascade floors & rooms
    }

    public Page<Building> listBuildings(Pageable pageable) {
        return buildingRepo.findAll(pageable);
    }
}
