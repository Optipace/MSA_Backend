package org.optipace.adminService.service;

import org.optipace.adminService.dto.response.SingleResponse;

import java.util.Map;

public interface AssessorDashboardService {

    SingleResponse<Map<String, Object>> getDashboardSummary();
}