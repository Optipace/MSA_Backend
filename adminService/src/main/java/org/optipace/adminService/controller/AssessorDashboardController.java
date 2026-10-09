package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.service.AssessorDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/assessor/dashboard")
@RequiredArgsConstructor
public class AssessorDashboardController {

    private final AssessorDashboardService assessorDashboardService;

    /**
     * GET /assessor/dashboard
     *
     * Returns:
     * - Total Employees
     * - Assessment Completed
     * - Assessment Pending
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<Map<String, Object>>> getDashboard() {

        log.info("REST request to get assessor dashboard");

        return ResponseEntity.ok(
                assessorDashboardService.getDashboardSummary()
        );
    }
}