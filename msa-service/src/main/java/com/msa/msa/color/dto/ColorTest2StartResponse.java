package com.msa.msa.color.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class ColorTest2StartResponse {

    private UUID assessmentModuleInstanceId;
    private String moduleCode;
    private String moduleName;
    private String status;
    private OffsetDateTime startedOn;
    private Integer timeLimitSeconds;

    public ColorTest2StartResponse(
            UUID assessmentModuleInstanceId,
            String moduleCode,
            String moduleName,
            String status,
            OffsetDateTime startedOn,
            Integer timeLimitSeconds) {

        this.assessmentModuleInstanceId = assessmentModuleInstanceId;
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
        this.status = status;
        this.startedOn = startedOn;
        this.timeLimitSeconds = timeLimitSeconds;
    }
}