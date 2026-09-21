package com.msa.msa.color.controller;

import com.msa.msa.assessment.dto.StartModuleRequest;
import com.msa.msa.color.dto.ColorTest2StartResponse;
import com.msa.msa.color.dto.ColorTest2SubmitRequest;
import com.msa.msa.color.dto.ColorTest2SubmitResponse;
import com.msa.msa.color.service.ColorTest2Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/color/test2")
public class ColorTest2Controller {

    private final ColorTest2Service colorTest2Service;

    public ColorTest2Controller(ColorTest2Service colorTest2Service) {
        this.colorTest2Service = colorTest2Service;
    }

    @PostMapping("/start")
    public ResponseEntity<ColorTest2StartResponse> startColorTest2(
            @Valid @RequestBody StartModuleRequest request) {

        return ResponseEntity.ok(
                colorTest2Service.startColorTest2(
                        request.getAssessmentSessionId(),
                        request.getAssessmentModuleInstanceId()
                )
        );
    }

    @PostMapping("/submit")
    public ResponseEntity<ColorTest2SubmitResponse> submitColorTest2(
            @Valid @RequestBody ColorTest2SubmitRequest request) {

        return ResponseEntity.ok(
                colorTest2Service.submitColorTest2(request)
        );
    }
}