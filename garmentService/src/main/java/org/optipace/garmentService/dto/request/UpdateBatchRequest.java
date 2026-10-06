package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateBatchRequest {

    @Size(max = 50)
    private String batchName;

    @Size(max = 30)
    private String batchCode;

    private String description;

    private Integer displayOrder;
}