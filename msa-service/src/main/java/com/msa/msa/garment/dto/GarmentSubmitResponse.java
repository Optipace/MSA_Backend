package com.msa.msa.garment.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GarmentSubmitResponse {

    private UUID assessmentResponseId;
    private Long batchId;
    private List<GarmentResult> garments;
    private String status;
    private String nextModule;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GarmentResult {

        private String garmentId;

        private int totalExpectedDefects;
        private int correctlyIdentifiedDefects;

        private int totalExpectedAreas;
        private int correctlyIdentifiedAreas;

        private BigDecimal defectScore;
        private BigDecimal areaScore;
        private BigDecimal combinedScore;

        private String result;
        private String status;
    }
}
