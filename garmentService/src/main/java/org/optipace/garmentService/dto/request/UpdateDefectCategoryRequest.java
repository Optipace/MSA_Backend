package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateDefectCategoryRequest {

    @Size(max = 30, message = "Category code must not exceed 30 characters")
    private String categoryCode;

    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String categoryName;

    private String description;

    private Integer displayOrder;
}