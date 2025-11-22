package com.erfan.VisitorManagement.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBuildingDto {
    @NotBlank
    private String name;
    private String address;
}

