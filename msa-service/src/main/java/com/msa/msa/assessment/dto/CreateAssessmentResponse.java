package com.msa.msa.assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssessmentResponse {

    private String assessmentSessionId;

    private String assessmentNo;

    private String status;

    private OffsetDateTime validUntil;

    private String currentModule;
}