package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefectCategoryResponseDTO {
    private Long defectCategoryId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Integer displayOrder;
    private OffsetDateTime createdOn;
}