package org.optipace.adminService.controller;
import java.util.UUID;

import org.optipace.adminService.dto.response.AssessorReportViewResponse;
import org.optipace.adminService.service.AssessorReportViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessor/reportview")
@CrossOrigin(origins = "*") // Adjust CORS origins as required
public class AssessorReportViewController {

    @Autowired
    private AssessorReportViewService assessmentService;

    @GetMapping("/empdetails/{sessionId}")
    public ResponseEntity<AssessorReportViewResponse> getAssessmentDetails(@PathVariable UUID sessionId) {
    	AssessorReportViewResponse response = assessmentService.getAssessmentDetails(sessionId);
        return ResponseEntity.ok(response);
    }
}