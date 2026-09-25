package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefectCategoryResponse {

    private Long defectCategoryId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Integer displayOrder;
}