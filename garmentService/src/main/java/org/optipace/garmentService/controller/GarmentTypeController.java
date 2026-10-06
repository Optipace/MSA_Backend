package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.optipace.garmentService.dto.request.AddGarmentTypeRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentTypeRequest;
import org.optipace.garmentService.dto.response.GarmentTypeResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentTypeResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.GarmentTypeService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/garmentType")
@RequiredArgsConstructor
public class GarmentTypeController {

    private final GarmentTypeService garmentTypeService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createGarmentType(
            @Valid @RequestBody AddGarmentTypeRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                garmentTypeService.createGarmentType(
                        request,
                        adminId
                )
        );
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfGarmentTypeResponse>>> getAllGarmentTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                garmentTypeService.getAllGarmentTypes(pageable)
        );
    }

    @GetMapping("/v1/{garmentTypeId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<GarmentTypeResponse>> getGarmentTypeById(
            @PathVariable Long garmentTypeId) {

        return ResponseEntity.ok(
                garmentTypeService.getGarmentTypeById(
                        garmentTypeId
                )
        );
    }

    @PatchMapping("/v1/update/{garmentTypeId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateGarmentType(
            @PathVariable Long garmentTypeId,
            @Valid @RequestBody UpdateGarmentTypeRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                garmentTypeService.updateGarmentType(
                        garmentTypeId,
                        request,
                        adminId
                )
        );
    }

    @DeleteMapping("/v1/delete/{garmentTypeId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteGarmentTypeById(
            @PathVariable Long garmentTypeId,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                garmentTypeService.deleteGarmentTypeById(
                        garmentTypeId,
                        adminId
                )
        );
    }
}