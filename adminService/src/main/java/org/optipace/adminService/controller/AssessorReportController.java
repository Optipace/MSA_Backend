
package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.service.AssessorReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/assessor/report")
@RequiredArgsConstructor
public class AssessorReportController {

    private final AssessorReportService assessorReportService;

    /**
     * GET /assessor/report?page=0&size=10
     *
     * Returns assessor report based on the logged-in user's
     * organization and factory access.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN', 'ASSESSOR')")
    public ResponseEntity<SingleResponse<Map<String, Object>>>
            getAssessorReport(
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size) {

        log.info(
                "REST request for assessor report: page={}, size={}",
                page, size
        );

        return ResponseEntity.ok(
                assessorReportService.getAssessorReport(page, size)
        );
    }
}
