
package com.msa.msa.garment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GarmentSubmitRequest {

    private UUID assessmentSessionId;
    private UUID assessmentModuleInstanceId;
    private Long batchId;

    private List<GarmentSubmission> garments;

    @Getter
    @Setter
    public static class GarmentSubmission {

        private String garmentId;
        private List<GarmentSelection> selections;
    }
}

