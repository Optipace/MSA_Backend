package org.optipace.adminService.repository;

import org.optipace.adminService.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface DashboardRepository {

    DashboardSummaryProjection getDashboardSummary();

    List<RecentAssessmentResponse> getRecentCompletedAssessments(int page, int size);

    long countRecentCompletedAssessments();

    List<CompetencyDistributionProjection> findCompetencyDistribution();

    Long countDistinctEmployeesWithCompetencyRating();

    List<MsaTrendPointResponse> getMsaTrend(LocalDate startDate, LocalDate endDate);

    List<TopMissedDefectProjection> getTopMissedDefects();

    List<TopMissedAreaProjection> getTopMissedAreas();

    List<Object[]> getTestDistribution(Long assessmentModuleId);

    Long countEmployeesAssessedForTest(Long assessmentModuleId);
}