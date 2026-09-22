package org.optipace.garmentService.dto.request;

import lombok.Data;

@Data
public class UpdateProductCategoryRequest {

    private String categoryCode;

    private String categoryName;

    private String description;

    private Integer displayOrder;

    private String remarks;
}