package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.request.AddShiftRequest;
import org.optipace.masterService.dto.request.UpdateShiftRequest;
import org.optipace.masterService.dto.response.ListOfShiftResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.ShiftResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.service.ShiftService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shift")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createShift(@Valid @RequestBody AddShiftRequest request,@RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(shiftService.createShift(request, adminId));
        }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfShiftResponse>>> getAllShift(
    		@RequestParam(defaultValue = "0") int page, 
    		@RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(shiftService.getAllShift(pageable));
        }

    @PatchMapping("/v1/update/{shiftId}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateShift(
            @PathVariable Long shiftId,
            @Valid @RequestBody UpdateShiftRequest request,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(shiftService.updateShift(shiftId, request, adminId));
        }

    @GetMapping("/v1/{shiftId}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<ShiftResponse>> getShiftById(@PathVariable Long shiftId) {
        return ResponseEntity.ok(shiftService.getShiftById(shiftId));
        }

    @DeleteMapping("/v1/delete/{shiftId}")
    @PreAuthorize("hasAuthority('FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteShiftById(
            @PathVariable Long shiftId,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(shiftService.deleteShiftById(shiftId,adminId));
        }
    }