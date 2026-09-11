package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SectionRequest {

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Factory ID is required")
    private Long factoryId;

    @NotBlank(message = "Section code is required")
    @Size(max = 30, message = "Section code must not exceed 30 characters")
    private String sectionCode;

    @NotBlank(message = "Section name is required")
    @Size(max = 150, message = "Section name must not exceed 150 characters")
    private String sectionName;

    @Min(value = 0, message = "Line number must be greater than or equal to 0")
    private Integer lineNumber;

    @Min(value = 0, message = "Capacity must be greater than or equal to 0")
    private Integer capacity;

    private Long createdBy;

    private Long updatedBy;

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    private String remarks;

    @NotNull(message = "Record status is required")
    private Character recordStatus;

}
