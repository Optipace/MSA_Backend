package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.service.AssessorReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/assessor/report")
@RequiredArgsConstructor
public class AssessorReportController {

    private final AssessorReportService assessorReportService;

    /**
     * GET /assessor/report
     *
     * Returns employee assessment reports.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<List<Map<String, Object>>>>
            getAssessorReport() {

        log.info("REST request to get assessor report");

        return ResponseEntity.ok(
                assessorReportService.getAssessorReport()
        );
    }
}