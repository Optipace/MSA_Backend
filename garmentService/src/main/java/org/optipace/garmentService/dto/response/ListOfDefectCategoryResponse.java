package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfDefectCategoryResponse {
    private Long defectCategoryId;
    private String categoryCode;
    private String categoryName;
    private Integer displayOrder;
    private String recordStatus;
}