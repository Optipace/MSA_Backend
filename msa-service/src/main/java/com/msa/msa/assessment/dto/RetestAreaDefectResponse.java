package com.msa.msa.assessment.dto;

import java.util.UUID;

public record RetestAreaDefectResponse(
        UUID oldAssessmentSessionId,
        UUID newAssessmentSessionId,
        UUID oldAssessmentModuleInstanceId,
        UUID newAssessmentModuleInstanceId,
        String moduleCode,
        String moduleStatus,
        String message
) {}