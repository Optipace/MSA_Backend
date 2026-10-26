package org.optipace.adminService.repository;

import org.optipace.adminService.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface DashboardRepository {

    DashboardSummaryProjection getDashboardSummary(Long organisationId, Long factoryId);

    List<RecentAssessmentResponse> getRecentCompletedAssessments(int page, int size,Long organisationId, Long factoryId);

    long countRecentCompletedAssessments(Long organisationId, Long factoryId);

    List<CompetencyDistributionProjection> findCompetencyDistribution(Long organisationId, Long factoryId);

    Long countDistinctEmployeesWithCompetencyRating(Long organisationId, Long factoryId);

    List<MsaTrendPointResponse> getMsaTrend(LocalDate startDate, LocalDate endDate,Long organisationId, Long factoryId);

    List<TopMissedDefectProjection> getTopMissedDefects(Long organisationId, Long factoryId);

    List<TopMissedAreaProjection> getTopMissedAreas(Long organisationId, Long factoryId);

    List<Object[]> getTestDistribution(Long assessmentModuleId,Long organisationId, Long factoryId);

    Long countEmployeesAssessedForTest(Long assessmentModuleId, Long organisationId, Long factoryId);
}