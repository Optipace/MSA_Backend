package com.msa.msa.color.controller;

import com.msa.msa.assessment.dto.StartModuleRequest;
import com.msa.msa.color.dto.ColorTest1StartResponse;
import com.msa.msa.color.dto.ColorTest1SubmitRequest;
import com.msa.msa.color.dto.ColorTest1SubmitResponse;
import com.msa.msa.color.service.ColorTest1Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/color/test1")
public class ColorTest1Controller {

    private final ColorTest1Service colorTest1Service;

    public ColorTest1Controller(ColorTest1Service colorTest1Service) {
        this.colorTest1Service = colorTest1Service;
    }

    @PostMapping("/start")
    public ResponseEntity<ColorTest1StartResponse> startColorTest1(
            @Valid @RequestBody StartModuleRequest request) {

        return ResponseEntity.ok(
                colorTest1Service.startColorTest1(
                        request.getAssessmentSessionId(),
                        request.getAssessmentModuleInstanceId()
                )
        );
    }

    @PostMapping("/submit")
    public ResponseEntity<ColorTest1SubmitResponse> submitColorTest1(
            @Valid @RequestBody ColorTest1SubmitRequest request) {

        return ResponseEntity.ok(
                colorTest1Service.submitColorTest1(request)
        );
    }
}