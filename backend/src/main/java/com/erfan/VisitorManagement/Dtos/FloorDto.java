package com.erfan.VisitorManagement.Dtos;

import com.erfan.VisitorManagement.Models.Floor;

public class FloorDto {
    public Long id;
    public Integer floorNumber;
    public Long buildingId;

    public static FloorDto fromEntity(Floor f) {
        FloorDto dto = new FloorDto();
        dto.id = f.getId();
        dto.floorNumber = f.getFloorNumber();
        dto.buildingId = f.getBuilding() != null ? f.getBuilding().getId() : null;
        return dto;
    }
}
