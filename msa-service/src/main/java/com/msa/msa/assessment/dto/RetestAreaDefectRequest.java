package com.msa.msa.assessment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RetestAreaDefectRequest {

    private UUID assessmentSessionId;

    private UUID assessmentModuleInstanceId;
}