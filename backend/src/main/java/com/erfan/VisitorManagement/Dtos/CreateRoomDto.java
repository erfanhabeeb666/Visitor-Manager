package com.erfan.VisitorManagement.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomDto {
    @NotNull
    private Long floorId;
    @NotBlank
    private String roomNumber;
}

