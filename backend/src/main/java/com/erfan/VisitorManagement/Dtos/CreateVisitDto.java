package com.erfan.VisitorManagement.Dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateVisitDto {

    @NotBlank
    private String visitorName;

    @NotBlank
    private String visitorPhone;

    @NotNull
    @FutureOrPresent
    private LocalDateTime visitDateTime;

    @NotNull
    @FutureOrPresent
    private LocalDateTime expiryTime;

    @NotNull
    private Long tenantId;
}
