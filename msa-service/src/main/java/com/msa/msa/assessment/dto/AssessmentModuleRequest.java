package com.msa.msa.assessment.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentModuleRequest {

    @NotBlank(message = "Module code is required")
    @Size(max = 50, message = "Module code must not exceed 50 characters")
    private String moduleCode;

    @NotBlank(message = "Module name is required")
    @Size(max = 100, message = "Module name must not exceed 100 characters")
    private String moduleName;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Is scored is required")
    private Boolean isScored;

    @NotNull(message = "Is mandatory is required")
    private Boolean isMandatory;

    @NotBlank(message = "Assessment type is required")
    private String assessmentType;
}