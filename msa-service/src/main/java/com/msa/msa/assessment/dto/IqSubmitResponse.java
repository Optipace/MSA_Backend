package com.msa.msa.assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IqSubmitResponse {

    private UUID assessmentModuleInstanceId;
    private String moduleCode;
    private BigDecimal completionTimeSeconds;
    private BigDecimal timeLimitSeconds;
    private String result;
    private String status;
    private String nextModule;
}