package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefectMasterResponse {

    private Long defectId;

    private Long defectCategoryId;

    private String defectCode;

    private String defectName;

    private String description;

    private Integer displayOrder;
}