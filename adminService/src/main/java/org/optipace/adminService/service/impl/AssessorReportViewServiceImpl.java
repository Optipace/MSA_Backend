package org.optipace.adminService.service.impl;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.optipace.adminService.dto.response.AssessorReportViewResponse;
import org.optipace.adminService.repository.AssessorReportViewRepository;
import org.optipace.adminService.service.AssessorReportViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssessorReportViewServiceImpl implements AssessorReportViewService {

    @Autowired
    private AssessorReportViewRepository assessorReportViewRepository;

    @Override
    public AssessorReportViewResponse getAssessmentDetails(UUID sessionId) {
        // Fetch summary metadata
        Map<String, Object> summary = assessorReportViewRepository.getAssessmentSummaryBySessionId(sessionId);
        
        // Fetch individual test breakdown
        List<Map<String, Object>> tests = assessorReportViewRepository.getAssessmentDetailsBySessionId(sessionId);

        return new AssessorReportViewResponse(summary, tests);
    }
}
