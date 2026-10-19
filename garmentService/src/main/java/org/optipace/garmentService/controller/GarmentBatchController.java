
package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.optipace.garmentService.dto.request.AddGarmentBatchRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentBatchRequest;
import org.optipace.garmentService.dto.response.GarmentBatchResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.GarmentBatchService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/garmentBatch")
@RequiredArgsConstructor
public class GarmentBatchController {

    private final GarmentBatchService garmentBatchService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createGarmentBatch(
            @Valid @RequestBody AddGarmentBatchRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                garmentBatchService.createGarmentBatch(request, adminId)
        );
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN', 'ASSESSOR')")
    public ResponseEntity<
            SingleResponse<PageResponse<GarmentBatchResponse>>>
            getAllGarmentBatches(
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                garmentBatchService.getAllGarmentBatches(pageable)
        );
    }

    @GetMapping("/v1/{garmentBatchId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN', 'ASSESSOR')")
    public ResponseEntity<SingleResponse<GarmentBatchResponse>>
            getGarmentBatchById(
                    @PathVariable Long garmentBatchId) {

        return ResponseEntity.ok(
                garmentBatchService.getGarmentBatchById(garmentBatchId)
        );
    }

    @PatchMapping("/v1/update/{garmentBatchId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateGarmentBatch(
            @PathVariable Long garmentBatchId,
            @Valid @RequestBody UpdateGarmentBatchRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                garmentBatchService.updateGarmentBatch(
                        garmentBatchId, request, adminId)
        );
    }
    @GetMapping("/batch/{batchId}/garments")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN', 'ASSESSOR')")
    public ResponseEntity<SingleResponse<List<GarmentBatchResponse>>>
            getGarmentsByBatchId(@PathVariable Long batchId) {

        return ResponseEntity.ok(
                garmentBatchService.getGarmentsByBatchId(batchId)
        );
    }
}
