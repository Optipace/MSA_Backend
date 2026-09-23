package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentExpectedDefectUpdateRequest {

    @NotNull(message = "Garment area ID is required")
    private Long garmentAreaId;

    @NotNull(message = "Defect ID is required")
    private Long defectId;

    @NotNull(message = "Severity level ID is required")
    private Long severityLevelId;
}