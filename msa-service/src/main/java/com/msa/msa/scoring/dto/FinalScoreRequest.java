package com.msa.msa.scoring.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class FinalScoreRequest {

    @NotNull
    private UUID assessmentSessionId;

    public UUID getAssessmentSessionId() {
        return assessmentSessionId;
    }

    public void setAssessmentSessionId(UUID assessmentSessionId) {
        this.assessmentSessionId = assessmentSessionId;
    }
}