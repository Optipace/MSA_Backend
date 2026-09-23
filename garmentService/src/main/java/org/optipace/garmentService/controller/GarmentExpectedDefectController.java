package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.garmentService.dto.request.GarmentExpectedDefectRequest;
import org.optipace.garmentService.dto.request.GarmentExpectedDefectUpdateRequest;
import org.optipace.garmentService.dto.response.GarmentExpectedDefectResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentExpectedDefectResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.GarmentExpectedDefectService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/garment-expected-defect")
@RequiredArgsConstructor
public class GarmentExpectedDefectController {

    private final GarmentExpectedDefectService garmentExpectedDefectService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createGarmentExpectedDefect(@Valid @RequestBody GarmentExpectedDefectRequest request, @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(garmentExpectedDefectService.createGarmentExpectedDefect(request, adminId));
    }

    @PatchMapping("/v1/{garmentExpectedDefectId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateGarmentExpectedDefect(@PathVariable UUID garmentExpectedDefectId, @Valid @RequestBody GarmentExpectedDefectUpdateRequest request) {
        return ResponseEntity.ok(garmentExpectedDefectService.updateGarmentExpectedDefect(garmentExpectedDefectId, request));
    }

    @GetMapping("/v1/{garmentExpectedDefectId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<GarmentExpectedDefectResponse>> getGarmentExpectedDefectById(@PathVariable UUID defect_id) {
        return ResponseEntity.ok(garmentExpectedDefectService.getGarmentExpectedDefectById(defect_id));
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfGarmentExpectedDefectResponse>>> getAllGarmentExpectedDefects(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(garmentExpectedDefectService.getAllGarmentExpectedDefects(pageable));
    }

//    @DeleteMapping("/v1/{garmentExpectedDefectId}")
//    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
//    public ResponseEntity<SingleResponse<?>> deleteGarmentExpectedDefect(@PathVariable UUID garmentExpectedDefectId) {
//        return ResponseEntity.ok(garmentExpectedDefectService.deleteGarmentExpectedDefect(garmentExpectedDefectId));
//    }
}