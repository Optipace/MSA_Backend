
package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.optipace.adminService.dto.response.FactoryScope;
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

    private final FactoryContextService factoryContextService;
    private final AssessorDashboardRepository assessorDashboardRepository;

    @Override
    public SingleResponse<Map<String, Object>> getAssessorDashboard(
            int page, int size) {

        log.info(
                "Fetching assessor dashboard: page={}, size={}",
                page, size
        );

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be zero or greater"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100"
            );
        }
        
        log.info("Resolving factory scope for assessor dashboard");

        FactoryScope scope =
                factoryContextService.getCurrentFactoryScope();

        Map<String, Object> result =
                assessorDashboardRepository.getAssessorDashboard(
                        scope.organisationId(),
                        scope.factoryId(),
                        page,
                        size
                );

        return new SingleResponse<>(
                result,
                CustomStatus.SUCCESS
        );
    }
}
