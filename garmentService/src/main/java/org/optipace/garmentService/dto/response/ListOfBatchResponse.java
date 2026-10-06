package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfBatchResponse {

    private Long batchId;
    private String batchName;
    private String batchCode;
    private Integer displayOrder;
}