package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class AddProductCategoryRequest {

    @NotBlank(message = "Category code is required")
    @Size(max = 30, message = "Category code cannot exceed 30 characters")
    private String categoryCode;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String categoryName;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Integer displayOrder;

    private String remarks;
}