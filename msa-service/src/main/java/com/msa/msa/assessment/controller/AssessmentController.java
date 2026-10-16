package com.msa.msa.assessment.controller;

import com.msa.msa.assessment.dto.*;

import java.util.UUID;

import com.msa.msa.assessment.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    public ResponseEntity<CreateAssessmentResponse> createAssessment(@Valid @RequestBody CreateAssessmentRequest request) {
        CreateAssessmentResponse response = assessmentService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/modules/start")
    public ResponseEntity<StartModuleResponse> startModule(@Valid @RequestBody StartModuleRequest request) {
        return ResponseEntity.ok(assessmentService.startModule(request.getAssessmentSessionId(), request.getAssessmentModuleInstanceId()));
    }

    @PostMapping("/progress")
    public ResponseEntity<AssessmentProgressResponse> getAssessmentProgress(@Valid @RequestBody AssessmentProgressRequest request) {
        return ResponseEntity.ok(assessmentService.getAssessmentProgress(request.getAssessmentSessionId()));
    }

    @PostMapping("/modules/iq/submit")
    public ResponseEntity<IqSubmitResponse> submitIqTest(@Valid @RequestBody IqSubmitRequest request) {
        return ResponseEntity.ok(assessmentService.submitIqTest(request));
    }

    @PostMapping("/modules/retest")
    public ResponseEntity<RetestModuleResponse> retestModule(@Valid @RequestBody RetestModuleRequest request) {
        return ResponseEntity.ok(assessmentService.retestModule(request));
    }

    @PostMapping("/retestArea-defect")
    public ResponseEntity<RetestAreaDefectResponse> retestAreaDefect(@RequestBody RetestAreaDefectRequest request) {
        return ResponseEntity.ok(assessmentService.retestAreaDefect(request));
    }

    @PostMapping("/modules/StartIQ")
    public ResponseEntity<StartModuleResponse> startIQTest(@Valid @RequestBody StartModuleRequest request) {
        return ResponseEntity.ok(assessmentService.startIQTest(request.getAssessmentSessionId(), request.getAssessmentModuleInstanceId()));
    }
}