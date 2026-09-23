package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddDefectMasterRequest {

    @NotNull(message = "Defect category ID is required")
    private Long defectCategoryId;

    @Size(max = 30, message = "Defect code cannot exceed 30 characters")
    private String defectCode;

    @Size(max = 200, message = "Defect name cannot exceed 200 characters")
    private String defectName;

    private String description;

    private Integer displayOrder;
}