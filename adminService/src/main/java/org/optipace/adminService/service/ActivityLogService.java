package org.optipace.adminService.service;

import org.optipace.adminService.dto.response.ActivityLogResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.entity.ActivityLog;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    // ⭐ Method to save an activity (Call this from your other services)
    public void logActivity(String type, String description, Long orgId, Long userId) {
        ActivityLog log = ActivityLog.builder()
                .activityType(type)
                .description(description)
                .organizationId(orgId)
                .createdBy(userId)
                .build();
        activityLogRepository.save(log);
    }

    // ⭐ Method to fetch recent activities for the UI
    public SingleResponse<List<ActivityLogResponse>> getRecentActivities() {
        List<ActivityLog> logs = activityLogRepository.findTop10ByOrderByCreatedOnDesc();

        List<ActivityLogResponse> response = logs.stream().map(log -> {
            return ActivityLogResponse.builder()
                    .activity(log.getDescription())
                    .time(calculateTimeAgo(log.getCreatedOn()))
                    .build();
        }).collect(Collectors.toList());

        return new SingleResponse<>(response, CustomStatus.SUCCESS);
    }

    // Helper to convert timestamp to "10 min ago"
    private String calculateTimeAgo(OffsetDateTime createdOn) {
        Duration duration = Duration.between(createdOn, OffsetDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) return "Just now";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " min ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";
        long days = hours / 24;
        return days + " days ago";
    }
}