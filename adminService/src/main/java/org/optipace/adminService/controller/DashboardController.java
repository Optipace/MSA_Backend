package org.optipace.adminService.controller;

import lombok.RequiredArgsConstructor;
import org.optipace.adminService.dto.response.*;
import org.optipace.adminService.service.impl.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

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

}