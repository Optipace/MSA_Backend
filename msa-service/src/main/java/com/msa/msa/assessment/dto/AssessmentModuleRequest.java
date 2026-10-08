package com.msa.msa.assessment.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentModuleRequest {

    @NotBlank(message = "Module code is required")
    private String moduleCode;

    @NotBlank(message = "Module name is required")
    private String moduleName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Display order is required")
    @Positive(message = "Display order must be greater than 0")
    private Integer displayOrder;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than 0 minutes")
    private Integer durationMinutes;

    @NotNull(message = "Scored status is required")
    private Boolean isScored;

    @NotNull(message = "Mandatory status is required")
    private Boolean isMandatory;
}