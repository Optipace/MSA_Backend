package org.optipace.garmentService.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefectCategoryRequestDTO {
    
    @NotBlank(message = "Category code is required")
    @Size(max = 30, message = "Category code must be less than 30 characters")
    private String categoryCode;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must be less than 100 characters")
    private String categoryName;
    
    private String description;
    
    private Integer displayOrder;
}