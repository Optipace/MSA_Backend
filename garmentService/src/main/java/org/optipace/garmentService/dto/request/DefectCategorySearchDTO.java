package org.optipace.garmentService.dto.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefectCategorySearchDTO {
    private String categoryCode;
    private String categoryName;
    private String description;
    private Integer displayOrder;
}