package org.optipace.garmentService.controller;

import java.util.List;

import org.optipace.garmentService.dto.request.GarmentDefectAreaSubmissionRequest;
import org.optipace.garmentService.dto.response.GarmentDefectAreaSubmissionResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.GarmentDefectAreaSubmissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/garmentDefectAreaSubmission/v1")
@RequiredArgsConstructor
public class GarmentDefectAreaSubmissionController {

    private final GarmentDefectAreaSubmissionService submissionService;

    @PostMapping("/submit")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<
            SingleResponse<List<GarmentDefectAreaSubmissionResponse>>> submit(
            @Valid @RequestBody GarmentDefectAreaSubmissionRequest request,
            @RequestHeader(value = "X-User-Id", required = false)
            String userId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(submissionService.submit(request, userId));
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN', 'ASSESSOR')")
    public ResponseEntity<
            SingleResponse<List<GarmentDefectAreaSubmissionResponse>>> getByBatchId(
            @PathVariable Long batchId) {

        return ResponseEntity.ok(
                submissionService.getByBatchId(batchId));
    }

    @GetMapping("/batch/{batchId}/garment/{garmentId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN', 'ASSESSOR')")
    public ResponseEntity<
            SingleResponse<List<GarmentDefectAreaSubmissionResponse>>>
            getByBatchIdAndGarmentId(
                    @PathVariable Long batchId,
                    @PathVariable String garmentId) {

        return ResponseEntity.ok(
                submissionService.getByBatchIdAndGarmentId(
                        batchId, garmentId));
    }
}