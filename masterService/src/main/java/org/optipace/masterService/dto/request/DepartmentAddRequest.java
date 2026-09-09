package org.optipace.masterService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DepartmentAddRequest {

    private String departmentCode;

    private String departmentName;

    private String description;
}
