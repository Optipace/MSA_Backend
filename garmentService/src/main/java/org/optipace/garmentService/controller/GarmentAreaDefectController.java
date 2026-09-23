package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.garmentService.dto.request.GarmentAreaDefectRequest;
import org.optipace.garmentService.dto.request.GarmentAreaDefectUpdateRequest;
import org.optipace.garmentService.dto.response.GarmentAreaDefectResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentAreaDefectResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.GarmentAreaDefectService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/garment-area-defect")
@RequiredArgsConstructor
public class GarmentAreaDefectController {

    private final GarmentAreaDefectService garmentAreaDefectService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createGarmentAreaDefect(@Valid @RequestBody GarmentAreaDefectRequest request, @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(garmentAreaDefectService.createGarmentAreaDefect(request, userId));
    }

    @GetMapping("/v1/{garmentAreaDefectId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<GarmentAreaDefectResponse>> getGarmentAreaDefectById(@PathVariable Long garmentAreaDefectId) {

        return ResponseEntity.ok(garmentAreaDefectService.getGarmentAreaDefectById(garmentAreaDefectId));
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfGarmentAreaDefectResponse>>> getAllGarmentAreaDefects(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(garmentAreaDefectService.getAllGarmentAreaDefects(pageable));
    }

    @PatchMapping("/v1/{garmentAreaDefectId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateGarmentAreaDefect(@PathVariable Long garmentAreaDefectId, @Valid @RequestBody GarmentAreaDefectUpdateRequest request, @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(garmentAreaDefectService.updateGarmentAreaDefect(garmentAreaDefectId, request, userId));
    }

    @DeleteMapping("/v1/{garmentAreaDefectId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteGarmentAreaDefect(@PathVariable Long garmentAreaDefectId, @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(garmentAreaDefectService.deleteGarmentAreaDefect(garmentAreaDefectId, userId));
    }
}