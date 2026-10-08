package org.optipace.adminService.service;
import org.optipace.adminService.dto.response.SingleResponse;

import java.util.List;
import java.util.Map;

public interface  OrganizationService {


	    // ⭐ Custom-query-based count methods
	    SingleResponse<Map<String, Object>> countBySuperAdmin(String adminId);
	    SingleResponse<List<Map<String, Object>>> countByAllAdmins();
	
}
