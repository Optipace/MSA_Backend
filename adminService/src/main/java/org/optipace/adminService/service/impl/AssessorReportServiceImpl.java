package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final AssessorReportRepository assessorReportRepository;

    @Override
    public SingleResponse<Map<String, Object>> getAssessorReport(
            int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to 0");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        log.debug(
                "Fetching assessor report: page={}, size={}",
                page, size);

        Map<String, Object> report =
                assessorReportRepository.getAssessorReport(page, size);

        return new SingleResponse<>(
                report,
                CustomStatus.SUCCESS
        );
    }
}