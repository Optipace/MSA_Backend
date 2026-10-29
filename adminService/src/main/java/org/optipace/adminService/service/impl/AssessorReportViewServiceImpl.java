package org.optipace.adminService.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.optipace.adminService.dto.response.AssessorReportViewResponse;
import org.optipace.adminService.repository.AssessorReportViewRepository;
import org.optipace.adminService.service.AssessorReportViewService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssessorReportViewServiceImpl
        implements AssessorReportViewService {

    private final AssessorReportViewRepository
            assessorReportViewRepository;

    @Override
    @Transactional(readOnly = true)
    public AssessorReportViewResponse getAssessmentDetails(
            String empCode) {

        // Find the employee's latest assessment session.
        UUID sessionId =
                assessorReportViewRepository
                        .getLatestSessionIdByEmpCode(empCode);

        // Fetch summary information for that session.
        Map<String, Object> summary =
                assessorReportViewRepository
                        .getAssessmentSummaryBySessionId(sessionId);

        // Fetch the individual test breakdown.
        List<Map<String, Object>> tests =
                assessorReportViewRepository
                        .getAssessmentDetailsBySessionId(sessionId);

        return new AssessorReportViewResponse(summary, tests);
    }
}
