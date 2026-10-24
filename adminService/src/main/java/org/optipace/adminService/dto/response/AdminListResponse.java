package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminListResponse {
	private Long employeeId;
	private String adminCode; // e.g., ADM001
	private String adminName; // e.g., Rahul Kumar
	private String email;
	private String phoneNumber;
	private String organizationName;
	private String location;       // 7
	private String roleName; // e.g., FACTORY_ADMIN
	private String status; // Active / Inactive
	
	
}
