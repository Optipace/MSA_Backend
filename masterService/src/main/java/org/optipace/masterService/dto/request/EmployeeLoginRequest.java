package org.optipace.masterService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLoginRequest {
	private Long employeeId;
	private String username;
	private String plainTextPassword; // ⭐ Must match the field name in authService!
	private Long roleId;
}
