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
     * - Average Score
     * - Employee Assessment Details
     */

@GetMapping
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN', 'ASSESSOR')")
public ResponseEntity<SingleResponse<Map<String, Object>>> getDashboard(
        @RequestParam Long organizationId,
        @RequestParam(required = false) Long factoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    if (page < 0 || size <= 0) {
        throw new IllegalArgumentException(
                "Page must be >= 0 and size must be > 0");
    }

    return ResponseEntity.ok(
            assessorDashboardService.getAssessorDashboard(
                    organizationId, factoryId, page, size)
    );
}

}