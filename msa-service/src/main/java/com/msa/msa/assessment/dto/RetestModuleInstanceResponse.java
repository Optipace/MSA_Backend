package com.msa.msa.assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetestModuleInstanceResponse {

    private UUID assessmentModuleInstanceId;
    private String moduleCode;
    private String moduleName;
    private String status;
    private Integer sequenceNo;
}
