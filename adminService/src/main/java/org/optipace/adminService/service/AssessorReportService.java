
package org.optipace.adminService.service;

import org.optipace.adminService.dto.response.SingleResponse;

import java.util.Map;

public interface AssessorReportService {

    SingleResponse<Map<String, Object>> getAssessorReport(
            int page,
            int size
    );
}
