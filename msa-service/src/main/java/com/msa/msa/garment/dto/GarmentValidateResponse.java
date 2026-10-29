
package com.msa.msa.garment.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GarmentValidateResponse {

    private boolean success;
    private String message;
    private BatchGarmentsData data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchGarmentsData {

        private Long batchId;

        private List<GarmentData> garments = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GarmentData {

        private String garmentId;

        private List<DefectData> defects = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefectData {

        private Long id;
        private String label;
        private List<AreaData> areas = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AreaData {

        private Long id;
        private String label;
        private boolean detected = false;
    }
}
