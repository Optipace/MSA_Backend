package org.optipace.garmentService.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GarmentDefectAreaSubmissionResponse {

    private Long submissionDetailId;
    private Long batchId;
    private String garmentId;
    private Long defectId;
    private Long areaId;
    private String createdBy;
    private LocalDateTime createdOn;
}