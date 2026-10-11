package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.repository.AssessorDashboardRepository;
import org.optipace.adminService.service.AssessorDashboardService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessorDashboardServiceImpl
        implements AssessorDashboardService {

    private final AssessorDashboardRepository assessorDashboardRepository;

    @Override
    public SingleResponse<Map<String, Object>> getAssessorDashboard() {

        log.info("Fetching assessor dashboard");

        Map<String, Object> result =
                assessorDashboardRepository.getAssessorDashboard();

        return new SingleResponse<>(
                result,
                CustomStatus.SUCCESS
        );
    }
}