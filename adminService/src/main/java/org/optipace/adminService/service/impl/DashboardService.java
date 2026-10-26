package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import org.optipace.adminService.dto.response.*;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.repository.AssessmentReportRepository;
import org.optipace.adminService.repository.DashboardRepository;
import org.optipace.adminService.repository.EmployeeReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final EmployeeReportRepository employeeReportRepository;
    private final AssessmentReportRepository assessmentReportRepository;
    private final FactoryContextService factoryContextService;

    public SingleResponse<DashboardSummaryProjection> getDashboardSummary() {
        FactoryScope scope = factoryContextService.getCurrentFactoryScope();
        DashboardSummaryProjection summary = dashboardRepository.getDashboardSummary(scope.organisationId(), scope.factoryId());
        return SingleResponse.success(summary, "Dashboard summary fetched successfully");
    }



    public SingleResponse<RecentAssessmentPageResponse>
    getRecentCompletedAssessments(int page, int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page must be >= 0 and size must be > 0");
        }

        int pageSize = Math.min(size, 100);

        FactoryScope scope =
                factoryContextService.getCurrentFactoryScope();

        Long organisationId = scope.organisationId();
        Long factoryId = scope.factoryId();

        List<RecentAssessmentResponse> data =
                dashboardRepository.getRecentCompletedAssessments(
                        page, pageSize, organisationId, factoryId);

        long totalElements =
                dashboardRepository.countRecentCompletedAssessments(
                        organisationId, factoryId);

        int totalPages =
                (int) Math.ceil((double) totalElements / pageSize);

        RecentAssessmentPageResponse response =
                new RecentAssessmentPageResponse(
                        data,
                        page,
                        pageSize,
                        totalElements,
                        totalPages,
                        page + 1 >= totalPages);

        return SingleResponse.success(
                response,
                "Recent completed assessments fetched successfully");
    }


    public SingleResponse<CompetencyDistributionResponse>
    getCompetencyDistribution() {

        FactoryScope scope =
                factoryContextService.getCurrentFactoryScope();

        Long organisationId = scope.organisationId();
        Long factoryId = scope.factoryId();

        try {
            List<CompetencyDistributionProjection> results =
                    dashboardRepository.findCompetencyDistribution(
                            organisationId, factoryId);

            Long totalEmployees =
                    dashboardRepository.countDistinctEmployeesWithCompetencyRating(
                            organisationId, factoryId);

            if (totalEmployees == null) {
                totalEmployees = 0L;
            }

            final Long total = totalEmployees;

            List<CompetencyDistributionItemResponse> distribution =
                    results.stream().map(row -> {

                        Long employeeCount =
                                row.getEmployeeCount() == null
                                        ? 0L
                                        : row.getEmployeeCount();

                        double percentage = total > 0
                                ? Math.round(
                                employeeCount * 10000.0 / total) / 100.0
                                : 0.0;

                        return new CompetencyDistributionItemResponse(
                                row.getCompetencyRatingId(),
                                row.getRatingCode(),
                                row.getRatingName(),
                                row.getColorCode(),
                                employeeCount,
                                percentage);
                    }).toList();

            CompetencyDistributionResponse response =
                    new CompetencyDistributionResponse(
                            totalEmployees, distribution);

            return new SingleResponse<>(response, CustomStatus.SUCCESS);

        } catch (Exception e) {
            // Log the exception using your project's logger if available.
            return new SingleResponse<>(null, CustomStatus.FAILURE);
        }
    }


    public SingleResponse<MsaTrendResponse> getMsaTrend(
            LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date must be greater than or equal to start date");
        }

        FactoryScope scope =
                factoryContextService.getCurrentFactoryScope();

        List<MsaTrendPointResponse> trend =
                dashboardRepository.getMsaTrend(
                        startDate,
                        endDate,
                        scope.organisationId(),
                        scope.factoryId());

        MsaTrendResponse response =
                new MsaTrendResponse(startDate, endDate, trend);

        return SingleResponse.success(
                response, "MSA trend fetched successfully");
    }


    public SingleResponse<TopMissedDefectsResponse>
    getTopMissedDefects() {

        FactoryScope scope =
                factoryContextService.getCurrentFactoryScope();

        List<TopMissedDefectProjection> results =
                dashboardRepository.getTopMissedDefects(
                        scope.organisationId(),
                        scope.factoryId());

        if (results == null || results.isEmpty()) {
            return SingleResponse.success(
                    new TopMissedDefectsResponse(Collections.emptyList()),
                    "No missed defects found");
        }

        List<TopMissedDefectItemResponse> defects = new ArrayList<>();
        int rank = 1;

        for (TopMissedDefectProjection row : results) {

            long missedCount = row.getMissedCount() == null
                    ? 0L : row.getMissedCount();

            long totalCount = row.getTotalCount() == null
                    ? 0L : row.getTotalCount();

            long employeeCount = row.getEmployeeCount() == null
                    ? 0L : row.getEmployeeCount();

            double percentage = totalCount > 0
                    ? Math.round(missedCount * 10000.0 / totalCount) / 100.0
                    : 0.0;

            defects.add(new TopMissedDefectItemResponse(
                    rank++,
                    row.getDefectName(),
                    percentage,
                    employeeCount));
        }

        return SingleResponse.success(
                new TopMissedDefectsResponse(defects),
                "Top missed defects retrieved successfully");
    }


    public SingleResponse<TopMissedAreasResponse>
    getTopMissedAreas() {

        FactoryScope scope =
                factoryContextService.getCurrentFactoryScope();

        List<TopMissedAreaProjection> results =
                dashboardRepository.getTopMissedAreas(
                        scope.organisationId(),
                        scope.factoryId());

        if (results == null || results.isEmpty()) {
            return SingleResponse.success(
                    new TopMissedAreasResponse(Collections.emptyList()),
                    "No missed areas found");
        }

        List<TopMissedAreaItemResponse> areas = new ArrayList<>();
        int rank = 1;

        for (TopMissedAreaProjection row : results) {

            long missedCount = row.getMissedCount() == null
                    ? 0L : row.getMissedCount();

            long totalCount = row.getTotalCount() == null
                    ? 0L : row.getTotalCount();

            long employeeCount = row.getEmployeeCount() == null
                    ? 0L : row.getEmployeeCount();

            double percentage = totalCount > 0
                    ? Math.round(missedCount * 10000.0 / totalCount) / 100.0
                    : 0.0;

            areas.add(new TopMissedAreaItemResponse(
                    rank++,
                    row.getAreaName(),
                    percentage,
                    employeeCount));
        }

        return SingleResponse.success(
                new TopMissedAreasResponse(areas),
                "Top missed areas retrieved successfully");
    }


    public SingleResponse<IqTestDistributionResponse>
    getIqTestDistribution() {

        FactoryScope scope =
                factoryContextService.getCurrentFactoryScope();

        Long organisationId = scope.organisationId();
        Long factoryId = scope.factoryId();

        try {
            Long assessmentModuleId = 1L;

            List<Object[]> results =
                    dashboardRepository.getTestDistribution(
                            assessmentModuleId, organisationId, factoryId);

            Long totalEmployees =
                    dashboardRepository.countEmployeesAssessedForTest(
                            assessmentModuleId, organisationId, factoryId);

            if (totalEmployees == null) {
                totalEmployees = 0L;
            }

            List<IqTestDistributionItemResponse> distribution =
                    new ArrayList<>();

            for (Object[] row : results) {

                Long categoryId = row[0] == null
                        ? null : ((Number) row[0]).longValue();

                String categoryName = row[1] == null
                        ? null : row[1].toString();

                Long employeeCount = row[2] == null
                        ? 0L : ((Number) row[2]).longValue();

                double percentage = totalEmployees > 0
                        ? Math.round(
                        employeeCount * 10000.0 / totalEmployees) / 100.0
                        : 0.0;

                distribution.add(new IqTestDistributionItemResponse(
                        categoryId,
                        categoryName,
                        employeeCount,
                        percentage));
            }

            IqTestDistributionResponse response =
                    new IqTestDistributionResponse(
                            totalEmployees, distribution);

            return SingleResponse.success(
                    response, "IQ Test distribution fetched successfully");

        } catch (Exception e) {
            // Log the exception using your project's logger if available.
            return new SingleResponse<>(null, CustomStatus.FAILURE);
        }
    }


    public SingleResponse<ColourTestDistributionResponse>
    getColourTestDistribution() {

        FactoryScope scope =
                factoryContextService.getCurrentFactoryScope();

        Long organisationId = scope.organisationId();
        Long factoryId = scope.factoryId();

        try {
            // Confirm that 2L is the correct Colour Test module ID.
            Long assessmentModuleId = 2L;

            List<Object[]> results =
                    dashboardRepository.getTestDistribution(
                            assessmentModuleId, organisationId, factoryId);

            Long totalEmployees =
                    dashboardRepository.countEmployeesAssessedForTest(
                            assessmentModuleId, organisationId, factoryId);

            if (totalEmployees == null) {
                totalEmployees = 0L;
            }

            List<ColourTestDistributionItemResponse> distribution =
                    new ArrayList<>();

            for (Object[] row : results) {

                Long categoryId = row[0] == null
                        ? null : ((Number) row[0]).longValue();

                String categoryName = row[1] == null
                        ? null : row[1].toString();

                Long employeeCount = row[2] == null
                        ? 0L : ((Number) row[2]).longValue();

                double percentage = totalEmployees > 0
                        ? Math.round(
                        employeeCount * 10000.0 / totalEmployees) / 100.0
                        : 0.0;

                distribution.add(new ColourTestDistributionItemResponse(
                        categoryId,
                        categoryName,
                        employeeCount,
                        percentage));
            }

            ColourTestDistributionResponse response =
                    new ColourTestDistributionResponse(
                            totalEmployees, distribution);

            return SingleResponse.success(
                    response,
                    "Colour Test distribution fetched successfully");

        } catch (Exception e) {
            // Log the exception using your project's logger if available.
            return new SingleResponse<>(null, CustomStatus.FAILURE);
        }
    }



    @Transactional(readOnly = true)
    public SingleResponse<EmployeeReportResponse> getEmployeeReport(
            int page, int size, String search) {

        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        String searchText =
                (search == null || search.isBlank()) ? null : search.trim();

        FactoryScope scope = factoryContextService.getCurrentFactoryScope();

        Pageable pageable = PageRequest.of(page, size);

        Page<EmployeeReportProjection> employeePage =
                employeeReportRepository.findEmployeeReport(
                        searchText,
                        pageable,
                        scope.organisationId(),
                        scope.factoryId());

        List<EmployeeReportItem> employees = employeePage.getContent()
                .stream()
                .map(record -> {
                    EmployeeReportItem item = new EmployeeReportItem();

                    item.setEmployeeId(record.getEmployeeId());
                    item.setEmployeeName(record.getEmployeeName());
                    item.setDepartment(record.getDepartment());
                    item.setFactory(record.getFactory());

                    item.setAssessment(
                            record.getCompletedAssessments()
                                    + "/"
                                    + record.getTotalAssessments());

                    item.setOverallScore(record.getOverallScore());
                    item.setResult(record.getResult());

                    return item;
                })
                .toList();

        EmployeeReportResponse response = new EmployeeReportResponse();

        response.setEmployees(employees);
        response.setTotalEmployees((int) employeePage.getTotalElements());
        response.setTotalPages(employeePage.getTotalPages());
        response.setCurrentPage(employeePage.getNumber());
        response.setPageSize(employeePage.getSize());

        return SingleResponse.success(response);
    }


    @Transactional(readOnly = true)
    public SingleResponse<EmployeeDetailsResponse> getEmployeeDetails(
            String employeeId) {

        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be blank");
        }

        FactoryScope scope = factoryContextService.getCurrentFactoryScope();

        EmployeeDetailsResponse response =
                employeeReportRepository.findEmployeeDetails(
                        employeeId.trim(),
                        scope.organisationId(),
                        scope.factoryId());

        if (response == null) {
            throw new IllegalArgumentException(
                    "Employee not found: " + employeeId);
        }

        return SingleResponse.success(response);
    }


    @Transactional(readOnly = true)
    public SingleResponse<List<AssessmentReportResponse>> getAssessmentReportList(
            int page, int size, String search) {

        if (page < 1) {
            throw new IllegalArgumentException(
                    "Page number must be at least 1");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        String searchText =
                (search == null || search.isBlank()) ? null : search.trim();

        FactoryScope scope = factoryContextService.getCurrentFactoryScope();

        List<AssessmentReportResponse> reports =
                assessmentReportRepository.findAssessmentReportList(
                        page,
                        size,
                        searchText,
                        scope.organisationId(),
                        scope.factoryId());

        return SingleResponse.success(reports);
    }


    @Transactional(readOnly = true)
    public SingleResponse<AssessmentDashboardResponse> getAssessmentReport(
            Long assessmentId, int page, int size, String search) {

        if (assessmentId == null || assessmentId <= 0) {
            throw new IllegalArgumentException(
                    "Assessment ID must be greater than zero");
        }

        if (page < 1) {
            throw new IllegalArgumentException(
                    "Page number must be at least 1");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        String searchText =
                (search == null || search.isBlank()) ? null : search.trim();

        FactoryScope scope = factoryContextService.getCurrentFactoryScope();

        Long organisationId = scope.organisationId();
        Long factoryId = scope.factoryId();

        PerformanceSummaryResponse summary =
                assessmentReportRepository.getPerformanceSummary(
                        assessmentId, organisationId, factoryId);

        // If no summary exists for this Factory Admin's scope,
        // do not allow access to another factory's assessment.
        if (summary == null) {
            throw new IllegalArgumentException(
                    "Assessment not found: " + assessmentId);
        }

        List<ScoreDistributionResponse> distribution =
                assessmentReportRepository.getScoreDistribution(
                        assessmentId, organisationId, factoryId);

        List<EmployeeAssessmentResponse> employeeResults =
                assessmentReportRepository.getEmployeeResults(
                        assessmentId,
                        page,
                        size,
                        searchText,
                        organisationId,
                        factoryId);

        AssessmentDashboardResponse response =
                AssessmentDashboardResponse.builder()
                        .performanceSummary(summary)
                        .scoreDistribution(distribution)
                        .employeeResults(employeeResults)
                        .build();

        return SingleResponse.success(response);
    }

}