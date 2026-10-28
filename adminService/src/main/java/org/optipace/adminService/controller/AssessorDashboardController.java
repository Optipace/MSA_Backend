
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
     * GET /assessor/dashboard?page=0&size=10
     *
     * Returns:
     * - Dashboard summary
     * - Paginated employee assessment details
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN', 'ASSESSOR')")
    public ResponseEntity<SingleResponse<Map<String, Object>>> getDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info(
                "REST request to get assessor dashboard: page={}, size={}",
                page, size
        );

        return ResponseEntity.ok(
                assessorDashboardService.getAssessorDashboard(page, size)
        );
    }
}
