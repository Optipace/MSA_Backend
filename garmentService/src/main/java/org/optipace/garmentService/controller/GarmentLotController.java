package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.garmentService.dto.request.GarmentLotRequest;
import org.optipace.garmentService.dto.request.GarmentLotUpdateRequest;
import org.optipace.garmentService.dto.response.GarmentLotResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentLotResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.GarmentLotService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/garment-lot")
@RequiredArgsConstructor
public class GarmentLotController {

    private final GarmentLotService garmentLotService;


    @PostMapping("/v1/add")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createGarmentLot(@Valid @RequestBody GarmentLotRequest request, @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(garmentLotService.createGarmentLot(request, adminId));
    }

    @PatchMapping("/v1/{lotNumber}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateGarmentLot(@PathVariable("lotNumber") String lotNumber, @Valid @RequestBody GarmentLotUpdateRequest request, @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(garmentLotService.updateGarmentLot(lotNumber, request, adminId));
    }


    @GetMapping("/v1/{lotNumber}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<GarmentLotResponse>> getGarmentLotByLotNumber(@PathVariable String lotNumber) {
        return ResponseEntity.ok(garmentLotService.getGarmentLotByLotNumber(lotNumber));
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfGarmentLotResponse>>> getAllGarmentLots(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(garmentLotService.getAllGarmentLots(pageable));
    }

    @DeleteMapping("/v1/{lotNumber}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteGarmentLot(@PathVariable("lotNumber") String lotNumber, @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(garmentLotService.deleteGarmentLotByLotNumber(lotNumber, adminId));
    }
}