package com.erfan.VisitorManagement.Dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TenantDecisionDto {
    @NotNull
    private Long visitId;

    @NotNull
    private String decision; // APPROVE or REJECT
}
