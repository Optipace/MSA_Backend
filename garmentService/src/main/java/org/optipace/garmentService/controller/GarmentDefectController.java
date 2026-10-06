package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.optipace.garmentService.dto.request.GarmentDefectSubmissionRequest;
import org.optipace.garmentService.dto.response.GarmentDefectSubmissionResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.GarmentDefectService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/garmentDefect")
@RequiredArgsConstructor
public class GarmentDefectController {

    private final GarmentDefectService garmentDefectService;

    @PostMapping("/v1/submit")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<GarmentDefectSubmissionResponse>>
            submitGarmentDefects(
                    @Valid @RequestBody GarmentDefectSubmissionRequest request,
                    @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                garmentDefectService.submitGarmentDefects(request, adminId)
        );
    }
}