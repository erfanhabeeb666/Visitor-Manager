package com.erfan.VisitorManagement.Dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFloorDto {
    @NotNull
    private Long buildingId;
    @NotNull
    private Integer floorNumber;
}
