package com.msa.msa.color.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ColorTest1SubmitRequest {

    @NotNull(message = "Assessment session ID is required")
    private UUID assessmentSessionId;

    @NotNull(message = "Assessment module instance ID is required")
    private UUID assessmentModuleInstanceId;

    @NotNull(message = "Correct answer count is required")
    private Integer correctAnswers;

    @NotNull(message = "Wrong answer count is required")
    private Integer wrongAnswers;
}