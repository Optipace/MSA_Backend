package org.optipace.adminService.controller;

import org.optipace.adminService.dto.response.AssessorReportViewResponse;
import org.optipace.adminService.service.AssessorReportViewService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessor/reportview")
@RequiredArgsConstructor
public class AssessorReportViewController {

    private final AssessorReportViewService assessmentService;

    @GetMapping("/empdetails/{empCode}")
    @PreAuthorize(
        "hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN', 'ASSESSOR')"
    )
    public ResponseEntity<AssessorReportViewResponse>
            getAssessmentDetails(
                    @PathVariable String empCode) {

        AssessorReportViewResponse response =
                assessmentService.getAssessmentDetails(empCode);

        return ResponseEntity.ok(response);
    }
}
