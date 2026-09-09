package org.optipace.masterService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EmployeeLoginDto {
    private Long employeeId;
    private String username;
    private String plainTextPassword;
    private Long roleId;
}
