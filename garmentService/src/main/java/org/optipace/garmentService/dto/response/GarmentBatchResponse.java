package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentBatchResponse {

    private Long garmentBatchId;
    private Long batchId;
    private Long productCategoryId;
    private String garmentId;
    private String garmentCode;
}