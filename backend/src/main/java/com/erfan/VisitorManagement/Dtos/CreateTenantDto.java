package com.erfan.VisitorManagement.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTenantDto {
    @NotBlank
    private String tenantName;
    @NotBlank
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number digits only")
    private String phoneNumber;
    @NotNull
    private Long roomId;
}

