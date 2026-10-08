package com.msa.msa.assessment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentModuleUpdateRequest {

    @Size(max = 50, message = "Module code must not exceed 50 characters")
    private String moduleCode;

    @Size(max = 100, message = "Module name must not exceed 100 characters")
    private String moduleName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private Boolean isScored;

    private Boolean isMandatory;
}