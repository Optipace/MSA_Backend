package com.msa.msa.assessment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class IqSubmitRequest {

    @NotNull(message = "Assessment session ID is required")
    private UUID assessmentSessionId;

    @NotNull(message = "Assessment module instance ID is required")
    private UUID assessmentModuleInstanceId;

    @NotNull(message = "Completion time is required")
    private BigDecimal completionTimeSeconds;
}