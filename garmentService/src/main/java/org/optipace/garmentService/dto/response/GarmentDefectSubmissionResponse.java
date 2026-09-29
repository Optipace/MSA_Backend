package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentDefectSubmissionResponse {

    private UUID garmentInstanceId;
    private String serialNumber;      // renamed from garmentIdentifier
    private Long batchId;
    private int defectRowsSaved;
}