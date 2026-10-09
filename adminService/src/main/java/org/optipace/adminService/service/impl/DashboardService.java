package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import org.optipace.adminService.dto.response.DashboardSummaryProjection;
import org.optipace.adminService.repository.DashboardRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardSummaryProjection getDashboardSummary() {
        return dashboardRepository.getDashboardSummary();
    }
}
