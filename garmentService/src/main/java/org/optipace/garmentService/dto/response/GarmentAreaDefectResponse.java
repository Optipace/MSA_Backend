package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentAreaDefectResponse {

    private Long garmentAreaDefectId;
    private Long garmentAreaId;
    private Long defectId;
    private Long severityLevelId;
    private BigDecimal maxDeductionMarks;
    private Boolean isMandatory;
    private BigDecimal aiConfidenceThreshold;
    private Long createdBy;
    private OffsetDateTime createdOn;
    private Long updatedBy;
    private OffsetDateTime updatedOn;
    private Integer versionNo;
    private String remarks;
    private String recordStatus;
}