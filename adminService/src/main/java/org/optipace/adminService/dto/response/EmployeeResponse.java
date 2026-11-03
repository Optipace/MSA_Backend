package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optipace.adminService.entity.Employee;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {


    private Long employeeId;
    private String employeeCode;
    private String firstName;
    private String email;
    private Long organizationId;
    private Long factoryId;

    public static EmployeeResponse from(Employee e) {
        return EmployeeResponse.builder()
                .employeeId(e.getEmployeeId())
                .employeeCode(e.getEmployeeCode())
                .firstName(e.getFirstName())
                .email(e.getEmail())
                .organizationId(e.getOrganizationId())
                .factoryId(e.getFactoryId())
                .build();
    }
    
}