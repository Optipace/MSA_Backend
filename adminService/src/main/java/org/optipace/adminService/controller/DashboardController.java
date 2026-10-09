package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import org.optipace.adminService.service.impl.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary() {
        return ResponseEntity.ok(
                dashboardService.getDashboardSummary()
        );
    }
}