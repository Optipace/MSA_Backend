package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.optipace.garmentService.dto.request.AddBatchRequest;
import org.optipace.garmentService.dto.request.UpdateBatchRequest;
import org.optipace.garmentService.dto.response.BatchResponse;
import org.optipace.garmentService.dto.response.ListOfBatchResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.BatchService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createBatch(
            @Valid @RequestBody AddBatchRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                batchService.createBatch(request, adminId)
        );
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<
            SingleResponse<PageResponse<ListOfBatchResponse>>>
            getAllBatches(
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                batchService.getAllBatches(pageable)
        );
    }

    @GetMapping("/v1/{batchId:\\d+}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<BatchResponse>>
            getBatchById(@PathVariable Long batchId) {

        return ResponseEntity.ok(
                batchService.getBatchById(batchId)
        );
    }

    @PatchMapping("/v1/update/{batchId:\\d+}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateBatch(
            @PathVariable Long batchId,
            @Valid @RequestBody UpdateBatchRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                batchService.updateBatch(batchId, request, adminId)
        );
    }

    @DeleteMapping("/v1/delete/{batchId:\\d+}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteBatchById(
            @PathVariable Long batchId,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                batchService.deleteBatchById(batchId, adminId)
        );
    }
}