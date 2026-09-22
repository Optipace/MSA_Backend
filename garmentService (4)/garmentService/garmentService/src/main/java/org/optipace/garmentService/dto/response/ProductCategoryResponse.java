package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponse {

    private Long productCategoryId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Integer displayOrder;
    private Long createdBy;
    private OffsetDateTime createdOn;
    private Long updatedBy;
    private OffsetDateTime updatedOn;
    private Integer versionNo;
    private String remarks;
    private String recordStatus;
}
