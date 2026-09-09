package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DesignationRequest {
    @NotBlank(message = "Provide designation code")
    @Size(max = 20, message = "Code cannot exceed 20 characters")
    private String designationCode;

    @NotBlank(message = "Provide designation name")
    @Size(max = 150, message = "Name cannot exceed 150 characters")
    private String designationName;

//    @NotNull(message = "Provide hierarchy level")
//    private Integer hierarchyLevel;

    private Boolean isManager;
    private String remarks;
}
