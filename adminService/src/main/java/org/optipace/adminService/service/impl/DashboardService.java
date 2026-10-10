package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import org.optipace.adminService.dto.response.*;
import org.optipace.adminService.enums.CustomStatus;
import org.optipace.adminService.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public SingleResponse<DashboardSummaryProjection> getDashboardSummary() {
        DashboardSummaryProjection summary = dashboardRepository.getDashboardSummary();
        return SingleResponse.success(summary, "Dashboard summary fetched successfully");
    }


    public SingleResponse<RecentAssessmentPageResponse> getRecentCompletedAssessments(int page, int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page must be >= 0 and size must be > 0");
        }

        int pageSize = Math.min(size, 100);

        List<RecentAssessmentResponse> data = dashboardRepository.getRecentCompletedAssessments(page, pageSize);
        long totalElements = dashboardRepository.countRecentCompletedAssessments();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        RecentAssessmentPageResponse response = new RecentAssessmentPageResponse(data, page, pageSize, totalElements, totalPages, page + 1 >= totalPages);
        return SingleResponse.success(response, "Recent completed assessments fetched successfully");
    }

    public SingleResponse<CompetencyDistributionResponse> getCompetencyDistribution() {

        try {
            List<CompetencyDistributionProjection> results = dashboardRepository.findCompetencyDistribution();

            Long totalEmployees = dashboardRepository.countDistinctEmployeesWithCompetencyRating();

            if (totalEmployees == null) {
                totalEmployees = 0L;
            }

            final Long total = totalEmployees;

            List<CompetencyDistributionItemResponse> distribution = results.stream().map(row -> {
                Long employeeCount = row.getEmployeeCount() == null ? 0L : row.getEmployeeCount();

                double percentage = total > 0 ? Math.round(employeeCount * 10000.0 / total) / 100.0 : 0.0;

                return new CompetencyDistributionItemResponse(row.getCompetencyRatingId(), row.getRatingCode(), row.getRatingName(), row.getColorCode(), employeeCount, percentage);
            }).toList();

            CompetencyDistributionResponse response = new CompetencyDistributionResponse(totalEmployees, distribution);

            return new SingleResponse<>(response, CustomStatus.SUCCESS);

        } catch (Exception e) {
            return new SingleResponse<>(null, CustomStatus.FAILURE);
        }
    }


    public SingleResponse<MsaTrendResponse> getMsaTrend(LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be greater than or equal to start date");
        }

        List<MsaTrendPointResponse> trend = dashboardRepository.getMsaTrend(startDate, endDate);
        MsaTrendResponse response = new MsaTrendResponse(startDate, endDate, trend);
        return SingleResponse.success(response, "MSA trend fetched successfully");
    }

    public SingleResponse<TopMissedDefectsResponse> getTopMissedDefects() {

        List<TopMissedDefectProjection> results =
                dashboardRepository.getTopMissedDefects();

        if (results == null || results.isEmpty()) {
            return SingleResponse.success(
                    new TopMissedDefectsResponse(Collections.emptyList()),
                    "No missed defects found"
            );
        }

        List<TopMissedDefectItemResponse> defects =
                new ArrayList<>();

        int rank = 1;

        for (TopMissedDefectProjection row : results) {

            long missedCount = row.getMissedCount() == null
                    ? 0L : row.getMissedCount();

            long totalCount = row.getTotalCount() == null
                    ? 0L : row.getTotalCount();

            long employeeCount = row.getEmployeeCount() == null
                    ? 0L : row.getEmployeeCount();

            double percentage = totalCount > 0
                    ? Math.round(
                    missedCount * 10000.0 / totalCount
            ) / 100.0
                    : 0.0;

            defects.add(new TopMissedDefectItemResponse(
                    rank++,
                    row.getDefectName(),
                    percentage,
                    employeeCount
            ));
        }

        return SingleResponse.success(
                new TopMissedDefectsResponse(defects),
                "Top missed defects retrieved successfully"
        );
    }

    public SingleResponse<TopMissedAreasResponse> getTopMissedAreas() {

        List<TopMissedAreaProjection> results =
                dashboardRepository.getTopMissedAreas();

        if (results == null || results.isEmpty()) {
            return SingleResponse.success(
                    new TopMissedAreasResponse(
                            Collections.emptyList()
                    ),
                    "No missed areas found"
            );
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
                    ? Math.round(
                    missedCount * 10000.0 / totalCount
            ) / 100.0
                    : 0.0;

            areas.add(new TopMissedAreaItemResponse(
                    rank++,
                    row.getAreaName(),
                    percentage,
                    employeeCount
            ));
        }

        return SingleResponse.success(
                new TopMissedAreasResponse(areas),
                "Top missed areas retrieved successfully"
        );
    }

    public SingleResponse<IqTestDistributionResponse> getIqTestDistribution() {

        try {
            List<Object[]> results =
                    dashboardRepository.getTestDistribution(1L);

            Long totalEmployees =
                    dashboardRepository.countEmployeesAssessedForTest(1L);

            if (totalEmployees == null) {
                totalEmployees = 0L;
            }

            List<IqTestDistributionItemResponse> distribution =
                    new ArrayList<>();

            for (Object[] row : results) {

                Long categoryId = row[0] == null
                        ? null
                        : ((Number) row[0]).longValue();

                String categoryName = row[1] == null
                        ? null
                        : row[1].toString();

                Long employeeCount = row[2] == null
                        ? 0L
                        : ((Number) row[2]).longValue();

                double percentage = totalEmployees > 0
                        ? Math.round(
                        employeeCount * 10000.0 / totalEmployees
                ) / 100.0
                        : 0.0;

                distribution.add(
                        new IqTestDistributionItemResponse(
                                categoryId,
                                categoryName,
                                employeeCount,
                                percentage
                        )
                );
            }

            IqTestDistributionResponse response =
                    new IqTestDistributionResponse(
                            totalEmployees,
                            distribution
                    );

            return SingleResponse.success(
                    response,
                    "IQ Test distribution fetched successfully"
            );

        } catch (Exception e) {
            return new SingleResponse<>(null, CustomStatus.FAILURE);
        }
    }

    public SingleResponse<ColourTestDistributionResponse> getColourTestDistribution() {

        try {
            // Replace 2L with the actual Colour Test assessment_module_id
            Long assessmentModuleId = 2L;

            List<Object[]> results =
                    dashboardRepository.getTestDistribution(assessmentModuleId);

            Long totalEmployees =
                    dashboardRepository.countEmployeesAssessedForTest(assessmentModuleId);

            if (totalEmployees == null) {
                totalEmployees = 0L;
            }

            List<ColourTestDistributionItemResponse> distribution =
                    new ArrayList<>();

            for (Object[] row : results) {

                Long categoryId = row[0] == null
                        ? null
                        : ((Number) row[0]).longValue();

                String categoryName = row[1] == null
                        ? null
                        : row[1].toString();

                Long employeeCount = row[2] == null
                        ? 0L
                        : ((Number) row[2]).longValue();

                double percentage = totalEmployees > 0
                        ? Math.round(
                        employeeCount * 10000.0 / totalEmployees
                ) / 100.0
                        : 0.0;

                distribution.add(
                        new ColourTestDistributionItemResponse(
                                categoryId,
                                categoryName,
                                employeeCount,
                                percentage
                        )
                );
            }

            ColourTestDistributionResponse response =
                    new ColourTestDistributionResponse(
                            totalEmployees,
                            distribution
                    );

            return SingleResponse.success(
                    response,
                    "Colour Test distribution fetched successfully"
            );

        } catch (Exception e) {
            return new SingleResponse<>(null, CustomStatus.FAILURE);
        }
    }

}