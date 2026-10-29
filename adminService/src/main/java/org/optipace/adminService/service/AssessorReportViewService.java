package org.optipace.adminService.service;

import java.util.UUID;

import org.optipace.adminService.dto.response.AssessorReportViewResponse;

public interface AssessorReportViewService {

    AssessorReportViewResponse getAssessmentDetails(String empCode);
}
