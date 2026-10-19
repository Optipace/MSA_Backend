package org.optipace.garmentService.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GarmentDefectAreaSubmissionRequest {

    @NotNull(message = "Batch ID is required")
    @Positive(message = "Batch ID must be greater than zero")
    private Long batchId;

    @NotEmpty(message = "At least one garment is required")
    @Valid
    private List<GarmentSubmissionRequest> garments;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GarmentSubmissionRequest {

        @NotBlank(message = "Garment ID is required")
        private String garmentId;

        @NotEmpty(message = "At least one defect entry is required")
        @Valid
        private List<DefectSubmissionRequest> defects;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DefectSubmissionRequest {

        // Optional: may be null
        @Positive(message = "Defect ID must be greater than zero")
        private Long defectId;

        // Optional: may be null, empty, or contain null entries
        private List<
                @Positive(message = "Area ID must be greater than zero")
                Long
        > areaIds;
    }
}