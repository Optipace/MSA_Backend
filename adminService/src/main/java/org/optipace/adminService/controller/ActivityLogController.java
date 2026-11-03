package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import org.optipace.adminService.dto.response.ActivityLogResponse;
import org.optipace.adminService.dto.response.EmployeeDetailsResponse;
import org.optipace.adminService.dto.response.EmployeeResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.service.ActivityLogService;
import org.optipace.adminService.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/activity-log")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final EmployeeService employeeService;

    // ⭐ Endpoint for the "Recent Activity" widget
    @GetMapping("/recent")
    public ResponseEntity<SingleResponse<List<ActivityLogResponse>>> getRecentActivities() {
        return ResponseEntity.ok(activityLogService.getRecentActivities());
    }
   
    @GetMapping("/team-info")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<List<EmployeeResponse>>> getTeamByUserId(
            @RequestParam Long userId) {
        return ResponseEntity.ok(employeeService.getTeamByUserId(userId));
    }
}