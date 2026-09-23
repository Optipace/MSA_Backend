package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfGarmentAreaDefectResponse {

    private Long garmentAreaDefectId;
    private Long garmentAreaId;
    private Long defectId;
    private Long severityLevelId;
    private BigDecimal maxDeductionMarks;
    private Boolean isMandatory;
    private BigDecimal aiConfidenceThreshold;
    private String remarks;
    private String recordStatus;
}