package com.msa.msa.assessment.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RetestModuleResponse {

    private UUID oldAssessmentModuleInstanceId;
    private UUID newAssessmentModuleInstanceId;
    private String moduleCode;
    private String moduleName;
    private String status;
    private OffsetDateTime createdOn;
}