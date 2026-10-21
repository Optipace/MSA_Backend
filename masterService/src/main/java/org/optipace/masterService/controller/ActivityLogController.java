package org.optipace.masterService.controller;

import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.response.ActivityLogResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity-log")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    // ⭐ Endpoint for the "Recent Activity" widget
    @GetMapping("/recent")
    public ResponseEntity<SingleResponse<List<ActivityLogResponse>>> getRecentActivities() {
        return ResponseEntity.ok(activityLogService.getRecentActivities());
    }
}