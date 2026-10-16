package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddGarmentBatchRequest {

    @NotNull(message = "Batch ID is required")
    private Long batchId;

    @NotNull(message = "Product category ID is required")
    private Long productCategoryId;

    @NotBlank(message = "Garment ID is required")
    @Size(max = 100, message = "Garment ID must not exceed 100 characters")
    private String garmentId;

    @NotBlank(message = "Garment name is required")
    @Size(max = 100, message = "Garment name must not exceed 100 characters")
    private String garmentName;
}