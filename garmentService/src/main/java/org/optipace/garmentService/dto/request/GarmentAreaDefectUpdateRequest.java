package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentAreaDefectUpdateRequest {
    @NotNull(message = "Garment area ID is required")
    private Long garmentAreaId;
    @NotNull(message = "Defect ID is required")
    private Long defectId;
    @NotNull(message = "Severity level ID is required")
    private Long severityLevelId;
    private BigDecimal maxDeductionMarks;
    private Boolean isMandatory;
    private BigDecimal aiConfidenceThreshold;
    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
    @NotNull(message = "Record status is required")
    private String recordStatus;
}