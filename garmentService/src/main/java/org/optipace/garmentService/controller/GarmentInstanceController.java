package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.garmentService.dto.request.GarmentInstanceCreateRequest;
import org.optipace.garmentService.dto.request.GarmentInstanceUpdateRequest;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.GarmentInstanceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/garment-instance")
@RequiredArgsConstructor
public class GarmentInstanceController {

    private final GarmentInstanceService garmentInstanceService;


    @PostMapping("/v1")
    public ResponseEntity<SingleResponse<?>> createGarmentInstance(
            @Valid @RequestBody GarmentInstanceCreateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return ResponseEntity.ok(garmentInstanceService.createGarmentInstance(request, userId));
    }

    @GetMapping("/v1/{serialNumber}")
    public ResponseEntity<SingleResponse<?>> getGarmentInstance(
            @PathVariable String serialNumber) {
        return ResponseEntity.ok(garmentInstanceService.getGarmentInstance(serialNumber));
    }

    @GetMapping("/v1")
    public ResponseEntity<SingleResponse<?>> getAllGarmentInstances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(garmentInstanceService.getAllGarmentInstances(pageable));
    }

    @PatchMapping("/v1/{serialNumber}")
    public ResponseEntity<SingleResponse<?>> updateGarmentInstance(
            @PathVariable String serialNumber,
            @Valid @RequestBody GarmentInstanceUpdateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return ResponseEntity.ok(garmentInstanceService.updateGarmentInstance(serialNumber, request, userId));
    }

    @DeleteMapping("/v1/{serialNumber}")
    public ResponseEntity<SingleResponse<?>> deleteGarmentInstance(
            @PathVariable String serialNumber,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return ResponseEntity.ok(garmentInstanceService.deleteGarmentInstance(serialNumber, userId));
    }
}