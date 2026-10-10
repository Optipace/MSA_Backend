package org.optipace.adminService.service;

import org.optipace.adminService.dto.response.SingleResponse;

import java.util.List;
import java.util.Map;

public interface AssessorReportService {

    SingleResponse<List<Map<String, Object>>> getAssessorReport();
}