
package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.FactoryScope;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.repository.AssessorReportRepository;
import org.optipace.adminService.service.AssessorReportService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessorReportServiceImpl implements AssessorReportService {

    private final FactoryContextService factoryContextService;
    private final AssessorReportRepository assessorReportRepository;

    @Override
    public SingleResponse<Map<String, Object>> getAssessorReport(
            int page,
            int size) {

        log.info(
                "Fetching assessor report: page={}, size={}",
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
        
        log.info("Resolving factory scope for assessor report");

        FactoryScope scope =
                factoryContextService.getCurrentFactoryScope();

        Map<String, Object> report =
                assessorReportRepository.getAssessorReport(
                        scope.organisationId(),
                        scope.factoryId(),
                        page,
                        size
                );

        return new SingleResponse<>(
                report,
                CustomStatus.SUCCESS
        );
    }
}
