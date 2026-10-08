package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.service.OrganizationCountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/organization/count")
@RequiredArgsConstructor
public class OrganizationCountController {
    
    private final OrganizationCountService organizationCountService;
    
    /**
     * GET /organization/count
     * Returns: { total, active, inactive, deleted }
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<Map<String, Object>>> getOrganizationCount() {
        log.info("REST request to get organization count");
        return ResponseEntity.ok(organizationCountService.getOrganizationCount());
    }
    
    /**
     * GET /organization/count/status/{status}
     * status = A / I / D
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<Long>> getCountByStatus(
            @PathVariable String status) {
        log.info("REST request to count organizations with status: {}", status);
        return ResponseEntity.ok(
            organizationCountService.getOrganizationCountByStatus(status)
        );
    }
    
    /**
     * GET /organization/count/by-admin
     * Uses X-User-Id header
     */
    @GetMapping("/by-admin")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<Long>> getCountByAdmin(
            @RequestHeader("X-User-Id") String adminId) {
        log.info("REST request to count organizations for admin: {}", adminId);
        return ResponseEntity.ok(
            organizationCountService.getOrganizationCountByAdmin(adminId)
        );
    }
}