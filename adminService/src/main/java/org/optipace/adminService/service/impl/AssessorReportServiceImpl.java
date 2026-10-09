package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.repository.AssessorReportRepository;
import org.optipace.adminService.service.AssessorReportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessorReportServiceImpl implements AssessorReportService {

    private final AssessorReportRepository assessorReportRepository;

    @Override
    public SingleResponse<List<Map<String, Object>>> getAssessorReport() {

        log.info("Fetching assessor report");

        List<Map<String, Object>> result =
                assessorReportRepository.getAssessorReport();

        return new SingleResponse<>(
                result,
                CustomStatus.SUCCESS
        );
    }
}