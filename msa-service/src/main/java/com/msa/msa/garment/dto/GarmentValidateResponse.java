package com.msa.msa.garment.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GarmentValidateResponse {

    private UUID assessmentSessionId;
    private UUID assessmentModuleInstanceId;
    private String moduleCode;
    private String moduleName;
    private String status;
    private OffsetDateTime startedOn;
    private boolean success;
    private String message;
    private BatchGarmentsData data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchGarmentsData {

        private Long batchId;
        private String batchName;
        private List<GarmentData> garments = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GarmentData {

        private String garmentId;
        private String garmentName;

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