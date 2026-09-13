package com.msa.msa.assessment.controller;

import com.msa.msa.assessment.dto.CreateAssessmentRequest;
import com.msa.msa.assessment.dto.AssessmentProgressRequest;
import com.msa.msa.assessment.dto.AssessmentProgressResponse;
import com.msa.msa.assessment.dto.StartModuleResponse;
import com.msa.msa.assessment.dto.StartModuleRequest;

import java.util.UUID;
import com.msa.msa.assessment.dto.CreateAssessmentResponse;
import com.msa.msa.assessment.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.msa.msa.assessment.dto.IqSubmitRequest;
import com.msa.msa.assessment.dto.IqSubmitResponse;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    public ResponseEntity<CreateAssessmentResponse> createAssessment(
            @Valid @RequestBody CreateAssessmentRequest request
    ) {
        CreateAssessmentResponse response =
                assessmentService.createAssessment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @PostMapping("/modules/start")
    public ResponseEntity<StartModuleResponse> startModule(
            @Valid @RequestBody StartModuleRequest request) {

        return ResponseEntity.ok(
                assessmentService.startModule(
                        request.getAssessmentSessionId(),
                        request.getAssessmentModuleInstanceId()
                )
        );
    }
    @PostMapping("/progress")
    public ResponseEntity<AssessmentProgressResponse> getAssessmentProgress(
            @Valid @RequestBody AssessmentProgressRequest request) {

        return ResponseEntity.ok(
                assessmentService.getAssessmentProgress(
                        request.getAssessmentSessionId()));
    }
    @PostMapping("/modules/iq/submit")
    public ResponseEntity<IqSubmitResponse> submitIqTest(
            @Valid @RequestBody IqSubmitRequest request) {

        return ResponseEntity.ok(
                assessmentService.submitIqTest(request)
        );
    }
}