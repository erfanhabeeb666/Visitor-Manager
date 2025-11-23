package com.erfan.VisitorManagement.Dtos;

import com.erfan.VisitorManagement.Models.Building;

public class BuildingDto {
    public Long id;
    public String name;
    public String address;

    public static BuildingDto fromEntity(Building b) {
        BuildingDto dto = new BuildingDto();
        dto.id = b.getId();
        dto.name = b.getName();
        dto.address = b.getAddress();
        return dto;
    }
}
