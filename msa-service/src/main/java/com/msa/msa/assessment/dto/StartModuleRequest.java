package com.msa.msa.assessment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class StartModuleRequest {

    @NotNull(message = "Assessment session ID is required")
    private UUID assessmentSessionId;

    @NotNull(message = "Assessment module instance ID is required")
    private UUID assessmentModuleInstanceId;
}