package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddGarmentTypeRequest {

    @NotNull(message = "Product category ID is required")
    private Long productCategoryId;

    @NotBlank(message = "Garment code is required")
    @Size(max = 30, message = "Garment code cannot exceed 30 characters")
    private String garmentCode;

    @NotBlank(message = "Garment name is required")
    @Size(max = 100, message = "Garment name cannot exceed 100 characters")
    private String garmentName;

    private String description;

    private String remarks;
}
