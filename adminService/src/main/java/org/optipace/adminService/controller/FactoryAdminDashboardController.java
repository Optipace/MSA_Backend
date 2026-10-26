package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.optipace.adminService.dto.response.*;
import org.optipace.adminService.service.impl.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class FactoryAdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<DashboardSummaryProjection>> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/recentAssessment")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<RecentAssessmentPageResponse>> getRecentCompletedAssessments(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dashboardService.getRecentCompletedAssessments(page, size));
    }

    @GetMapping("/competencyDistribution")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<CompetencyDistributionResponse>> getCompetencyDistribution() {
        return ResponseEntity.ok(dashboardService.getCompetencyDistribution());
    }

    @GetMapping("/msaTrend")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<MsaTrendResponse>> getMsaTrend(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(dashboardService.getMsaTrend(startDate, endDate));
    }

    @GetMapping("/topMissedDefects")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<TopMissedDefectsResponse>> getTopMissedDefects() {
        return ResponseEntity.ok(dashboardService.getTopMissedDefects());
    }

    @GetMapping("/topMissedAreas")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<TopMissedAreasResponse>> getTopMissedAreas() {
        return ResponseEntity.ok(dashboardService.getTopMissedAreas());
    }

    @GetMapping("/iqTest")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<IqTestDistributionResponse>> getIqTestDistribution() {
        return ResponseEntity.ok(dashboardService.getIqTestDistribution());
    }

    @GetMapping("/colourTest")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<ColourTestDistributionResponse>> getColourTestDistribution() {
        return ResponseEntity.ok(dashboardService.getColourTestDistribution());
    }

    @GetMapping("/employeeReport")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<EmployeeReportResponse>> getEmployeeReport(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(dashboardService.getEmployeeReport(page, size, search));
    }

    @GetMapping("/employeeReport/{employeeId}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<EmployeeDetailsResponse>> getEmployeeReportDetails(@PathVariable String employeeId) {
        return ResponseEntity.ok(dashboardService.getEmployeeDetails(employeeId));
    }

    @GetMapping("/assessmentReport")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<List<AssessmentReportResponse>>> getAssessmentReportList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(dashboardService.getAssessmentReportList(page, size, search));
    }

    @GetMapping("/assessmentReport/{assessmentId}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<AssessmentDashboardResponse>> getAssessmentReport(@PathVariable Long assessmentId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(dashboardService.getAssessmentReport(assessmentId, page, size, search));
    }
}