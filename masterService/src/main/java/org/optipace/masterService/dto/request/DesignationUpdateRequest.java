package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DesignationUpdateRequest {
    @Size(max = 20, message = "Code cannot exceed 20 characters")
    private String designationCode;

    @Size(max = 150, message = "Name cannot exceed 150 characters")
    private String designationName;

//    private Integer hierarchyLevel;
    private Boolean isManager;
    private String remarks;
}
