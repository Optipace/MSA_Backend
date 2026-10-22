package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.optipace.adminService.dto.response.*;
import org.optipace.adminService.service.impl.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class FactoryAdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<SingleResponse<DashboardSummaryProjection>> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/recentAssessment")
    public ResponseEntity<SingleResponse<RecentAssessmentPageResponse>> getRecentCompletedAssessments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dashboardService.getRecentCompletedAssessments(page, size));
    }

    @GetMapping("/competencyDistribution")
    public ResponseEntity<SingleResponse<CompetencyDistributionResponse>> getCompetencyDistribution() {
        return ResponseEntity.ok(dashboardService.getCompetencyDistribution());
    }

    @GetMapping("/msaTrend")
    public ResponseEntity<SingleResponse<MsaTrendResponse>> getMsaTrend(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(dashboardService.getMsaTrend(startDate, endDate));
    }

    @GetMapping("/topMissedDefects")
    public ResponseEntity<SingleResponse<TopMissedDefectsResponse>> getTopMissedDefects() {
        return ResponseEntity.ok(dashboardService.getTopMissedDefects());
    }

    @GetMapping("/topMissedAreas")
    public ResponseEntity<SingleResponse<TopMissedAreasResponse>> getTopMissedAreas() {
        return ResponseEntity.ok(dashboardService.getTopMissedAreas());
    }

    @GetMapping("/iqTest")
    public ResponseEntity<SingleResponse<IqTestDistributionResponse>> getIqTestDistribution() {
        return ResponseEntity.ok(dashboardService.getIqTestDistribution());
    }

    @GetMapping("/colourTest")
    public ResponseEntity<SingleResponse<ColourTestDistributionResponse>> getColourTestDistribution() {
        return ResponseEntity.ok(dashboardService.getColourTestDistribution());
    }

    @GetMapping("/employeeReport")
    public ResponseEntity<SingleResponse<EmployeeReportResponse>> getEmployeeReport(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(dashboardService.getEmployeeReport(page, size, search));
    }

    @GetMapping("/employeeReport/{employeeId}")
    public ResponseEntity<SingleResponse<EmployeeDetailsResponse>> getEmployeeReportDetails(@PathVariable String employeeId) {
        return ResponseEntity.ok(dashboardService.getEmployeeDetails(employeeId));
    }

    @GetMapping("/assessmentReport")
    public ResponseEntity<SingleResponse<List<AssessmentReportResponse>>> getAssessmentReportList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(dashboardService.getAssessmentReportList(page, size, search));
    }

    @GetMapping("/assessmentReport/{assessmentId}")
    public ResponseEntity<SingleResponse<AssessmentDashboardResponse>> getAssessmentReport(@PathVariable Long assessmentId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(dashboardService.getAssessmentReport(assessmentId, page, size, search));
    }
}