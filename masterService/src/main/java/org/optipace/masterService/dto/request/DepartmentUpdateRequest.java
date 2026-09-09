package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentUpdateRequest {

    @Size(max = 20)
    private String departmentCode;

    @Size(max = 100)
    private String departmentName;

    private String description;

    private String remarks;
}
