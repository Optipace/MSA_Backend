
package org.optipace.adminService.service.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
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
    public AssessorReportViewResponse getAssessmentDetails(String empCode) {

        UUID sessionId =
                assessorReportViewRepository.getLatestSessionIdByEmpCode(empCode);

        if (sessionId == null) {

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("employeeCode", empCode);
            summary.put("message", "No test has been assigned to this employee");
            summary.put("overallScore", 0);
            summary.put("totalModules", 0);
            summary.put("completedModules", 0);
            summary.put("passedCount", 0);
            summary.put("failedCount", 0);

            return new AssessorReportViewResponse(
                    summary,
                    Collections.emptyList()
            );
        }

        Map<String, Object> summary =
                assessorReportViewRepository.getAssessmentSummaryBySessionId(sessionId);

        List<Map<String, Object>> tests =
                assessorReportViewRepository.getAssessmentDetailsBySessionId(sessionId);

        return new AssessorReportViewResponse(summary, tests);
    }
}
