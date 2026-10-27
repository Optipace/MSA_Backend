package org.optipace.adminService.service;

import org.optipace.adminService.dto.response.SingleResponse;

import java.util.Map;

public interface AssessorReportService {

    SingleResponse<Map<String, Object>> getAssessorReport(
            Long organizationId,
            Long factoryId,
            int page,
            int size);
}
