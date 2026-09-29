package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchResponse {

    private Long batchId;
    private String batchName;
    private String batchCode;
    private String description;
    private Integer displayOrder;
    private String recordStatus;
    private String createdBy;
    private String updatedBy;
    private OffsetDateTime createdOn;
    private OffsetDateTime updatedOn;
}