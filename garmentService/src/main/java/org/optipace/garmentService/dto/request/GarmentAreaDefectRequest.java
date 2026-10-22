package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentAreaDefectRequest {
	
	@NotNull(message = "Garment batch ID is required")
	private Long garmentBatchId;

    @NotNull(message = "Garment area ID is required")
    private Long garmentAreaId;

    @NotNull(message = "Defect ID is required")
    private Long defectId;

    private Long severityLevelId;

    @DecimalMin(value = "0.0",  message = "Max deduction marks must be greater than or equal to 0")
    private BigDecimal maxDeductionMarks;

    private Boolean isMandatory;

    private BigDecimal aiConfidenceThreshold;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}