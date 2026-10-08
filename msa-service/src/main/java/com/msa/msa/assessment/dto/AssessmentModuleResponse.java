package com.msa.msa.assessment.dto;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class AssessmentModuleResponse {
    private Long assessmentModuleId;
    private String moduleCode;
    private String moduleName;
    private String description;
    private Integer displayOrder;
    private Integer durationMinutes;
    private Boolean isScored;
    private Boolean isMandatory;
    private OffsetDateTime createdOn;
    private OffsetDateTime updatedOn;
    private char recordStatus;
}