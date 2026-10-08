package com.msa.msa.assessment.controller;

import com.msa.msa.assessment.dto.AssessmentModuleRequest;
import com.msa.msa.assessment.dto.AssessmentModuleResponse;
import com.msa.msa.assessment.dto.PageResponse;
import com.msa.msa.assessment.dto.SingleResponse;
import com.msa.msa.assessment.service.AssessmentModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assessmentModule")
@RequiredArgsConstructor
public class AssessmentModuleController {

    private final AssessmentModuleService assessmentModuleService;

    @PostMapping("/v1/add")
    public ResponseEntity<SingleResponse<?>> createModule(@Valid @RequestBody AssessmentModuleRequest request, @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(assessmentModuleService.createModule(request, adminId));
    }

    @GetMapping("/v1/all")
    public ResponseEntity<SingleResponse<PageResponse<AssessmentModuleResponse>>> getAllModules(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(assessmentModuleService.getAllModules(pageable));
    }

    @GetMapping("/v1/{moduleId}")
    public ResponseEntity<SingleResponse<AssessmentModuleResponse>> getModuleById(@PathVariable Long moduleId) {
        return ResponseEntity.ok(assessmentModuleService.getModuleById(moduleId));
    }

    @PatchMapping("/v1/update/{moduleId}")
    public ResponseEntity<SingleResponse<?>> updateModule(@PathVariable Long moduleId, @Valid @RequestBody AssessmentModuleRequest request, @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(assessmentModuleService.updateModule(moduleId, request, adminId));
    }

    @DeleteMapping("/v1/delete/{moduleId}")
    public ResponseEntity<SingleResponse<?>> deleteModule(@PathVariable Long moduleId){
        return ResponseEntity.ok(assessmentModuleService.deleteModule(moduleId));
    }

}