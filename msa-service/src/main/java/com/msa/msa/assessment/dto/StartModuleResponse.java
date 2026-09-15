package com.msa.msa.assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartModuleResponse {

    private UUID assessmentModuleInstanceId;
    private String moduleCode;
    private String moduleName;
    private String status;
    private OffsetDateTime startedOn;
}