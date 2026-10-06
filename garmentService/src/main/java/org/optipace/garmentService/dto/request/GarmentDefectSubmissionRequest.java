package org.optipace.garmentService.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentDefectSubmissionRequest {

    @NotNull(message = "Batch ID is required")
    private Long batchId;

    @NotNull(message = "Garment lot ID is required")
    private UUID garmentLotId;                   // ← UUID, not Long

    /**
     * Whatever the user typed or scanned in "Garment ID or barcode".
     * If it starts with a numeric barcode pattern, we store it as barcode;
     * otherwise we store it as serial_number.
     * Or you can add an explicit "identifierType" field if the frontend knows.
     */
    @NotBlank(message = "Garment ID or barcode is required")
    private String garmentIdentifier;

    private String currentLocation;              // optional
    private String remarks;                      // optional

    @NotEmpty(message = "At least one defect must be selected")
    @Valid
    private List<DefectSelection> defects;

    // -------- nested DTO --------

    @Data
    public static class DefectSelection {

        @NotNull(message = "Defect ID is required")
        private Long defectId;

        @NotNull(message = "Severity level is required")
        private Long severityLevelId;

        @NotEmpty(message = "Each defect must have at least one area")
        private List<Long> areaIds;
    }
}