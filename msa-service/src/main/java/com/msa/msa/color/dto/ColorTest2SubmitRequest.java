package com.msa.msa.color.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ColorTest2SubmitRequest {

    @NotNull
    private UUID assessmentSessionId;

    @NotNull
    private UUID assessmentModuleInstanceId;

    @NotNull
    private Integer correctAnswers;

    @NotNull
    private Integer wrongAnswers;
}